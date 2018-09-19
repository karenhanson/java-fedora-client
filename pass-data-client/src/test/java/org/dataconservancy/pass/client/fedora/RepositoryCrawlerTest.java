/*
 * Copyright 2017 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dataconservancy.pass.client.fedora;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.dataconservancy.pass.client.fedora.FedoraConfig.getBaseUrl;
import static org.dataconservancy.pass.client.fedora.RepositoryCrawler.endWithSlash;
import static org.dataconservancy.pass.client.fedora.RepositoryCrawler.Ignore.IGNORE_CONTAINERS;
import static org.dataconservancy.pass.client.fedora.RepositoryCrawler.Ignore.IGNORE_NONE;
import static org.dataconservancy.pass.client.fedora.RepositoryCrawler.Ignore.IGNORE_ROOT;
import static org.dataconservancy.pass.client.fedora.RepositoryCrawler.Skip.SKIP_ACLS;
import static org.dataconservancy.pass.client.fedora.RepositoryCrawler.Skip.SKIP_NONE;
import static org.dataconservancy.pass.client.fedora.RepositoryCrawler.Skip.depth;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.dataconservancy.pass.client.fedora.RepositoryCrawler.State;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author apb@jhu.edu
 */
@RunWith(MockitoJUnitRunner.class)
public class RepositoryCrawlerTest {

    @Mock
    Lister lister;

    final URI root = URI.create(getBaseUrl());

    final URI l1_acls_container = URI.create(endWithSlash(getBaseUrl()) + "acls/");

    final URI l1_submissions_container = URI.create(endWithSlash(getBaseUrl()) + "submissions/");

    final URI l1_cows_container = URI.create(endWithSlash(getBaseUrl()) + "cows/");

    final Collection<URI> l1_all = asList(l1_acls_container, l1_submissions_container, l1_cows_container);

    final URI l2_acl_1 = randomUri(l1_acls_container);

    final URI l2_acl_2 = randomUri(l1_acls_container);

    final Collection<URI> l2_acls = asList(l2_acl_1, l2_acl_2);

    final URI l2_cow_1 = randomUri(l1_cows_container);

    final URI l2_cow_2 = randomUri(l1_cows_container);

    final Collection<URI> l2_cows = asList(l2_cow_1, l2_cow_2);

    final URI l2_submissions_1 = randomUri(l1_submissions_container);

    final URI l2_submissions_2 = randomUri(l1_submissions_container);

    final Collection<URI> l2_submissions = asList(l2_submissions_1, l2_submissions_2);

    final Collection<URI> l2_all = union(l2_acls, l2_cows, l2_submissions);

    final URI l3_acl_1 = randomUri(l2_acl_1);

    final URI l3_acl_2 = randomUri(l2_acl_2);

    final Collection<URI> l3_acls = asList(l3_acl_1, l3_acl_2);

    final URI l3_submissions_1 = randomUri(l2_submissions_1);

    final URI l3_submissions_2 = randomUri(l2_submissions_2);

    final Collection<URI> l3_aubmissions = asList(l3_submissions_1, l3_submissions_2);

    final Collection<URI> l3_all = union(l3_acls, l3_aubmissions);

    final RepositoryCrawler toTest = new RepositoryCrawler();

    @Before
    public void wire() {
        toTest.repo = lister;

        // default - no children
        when(lister.getChildren(any())).thenReturn(emptyList());

        // Now we define our tree
        when(lister.getChildren(eq(root))).thenReturn(l1_all);
        when(lister.getChildren(eq(l1_submissions_container))).thenReturn(l2_submissions);
        when(lister.getChildren(eq(l1_acls_container))).thenReturn(l2_acls);
        when(lister.getChildren(eq(l1_cows_container))).thenReturn(l2_cows);
        when(lister.getChildren(eq(l2_acl_1))).thenReturn(asList(l3_acl_1));
        when(lister.getChildren(eq(l2_acl_2))).thenReturn(asList(l3_acl_2));
        when(lister.getChildren(eq(l2_submissions_1))).thenReturn(asList(l3_submissions_1));
        when(lister.getChildren(eq(l2_submissions_2))).thenReturn(asList(l3_submissions_2));
    }

    // Verify that zero depth just retrieves one resource, and doesn't fetch children.
    @Test
    public void zeroDepthTest() {

        final List<URI> visited = new ArrayList<>();
        assertEquals(1, toTest.visit(root, visited::add, IGNORE_NONE, depth(0)));
        assertEquals(1, visited.size());
        assertEquals(root, visited.get(0));
        verify(lister, times(0)).getChildren(any());
    }

    // Verify that we can limit depth
    @Test
    public void depthTest() {
        final List<URI> visited = new ArrayList<>();

        // Root + all on level 1
        final Collection<URI> upToL1 = union(asList(root), l1_all);

        assertEquals(upToL1.size(), toTest.visit(root, visited::add, IGNORE_NONE, depth(1)));
        assertEquals(upToL1.size(), visited.size());
        verify(lister, times(1)).getChildren(any());

        assertTrue(visited.containsAll(upToL1));
    }

    // Verify that all resources are visited if we start from root
    @Test
    public void entireRepositoryTest() {
        final List<URI> visited = new ArrayList<>();

        final Collection<URI> allResources = union(asList(root), l1_all, l2_all, l3_all);

        assertEquals(allResources.size(), toTest.visit(root, visited::add, IGNORE_NONE, SKIP_NONE));
        assertEquals(allResources.size(), visited.size());
    }

    // Verify that ignore IGNORE_ROOT will skip the parent container, and works with depth
    @Test
    public void ignoreRootTestWithDepth() {
        final List<URI> visited = new ArrayList<>();

        assertEquals(l2_submissions.size(), toTest.visit(l1_submissions_container, visited::add,
                IGNORE_ROOT, depth(1)));
        assertEquals(l2_submissions.size(), visited.size());
        assertTrue(visited.containsAll(l2_submissions));
        assertFalse(visited.contains(l1_submissions_container));

        // Should only list resources of parent container, so one invocation ony;
        verify(lister, times(1)).getChildren(any());
    }

    // Verify that IGNORE CONTAINERS will skip top level containers
    @Test
    public void skipContainerTestInfiniteDepth() {
        final List<URI> visited = new ArrayList<>();

        final Collection<URI> allExceptContainers = union(l2_all, l3_all);

        assertEquals(allExceptContainers.size(), toTest.visit(root, visited::add,
                IGNORE_CONTAINERS,
                SKIP_NONE));
        assertEquals(allExceptContainers.size(), visited.size());
        assertTrue(visited.containsAll(allExceptContainers));
    }

    @Test
    public void skipAclsTest() {
        final List<URI> visited = new ArrayList<>();

        final Collection<URI> allExceptAclsAndContainers = union(l2_cows, l2_submissions, l3_aubmissions);

        assertEquals(allExceptAclsAndContainers.size(), toTest.visit(root, visited::add, IGNORE_CONTAINERS,
                SKIP_ACLS));
        assertEquals(allExceptAclsAndContainers.size(), visited.size());
        assertTrue(visited.containsAll(allExceptAclsAndContainers));
    }

    @Test
    public void aclRegexTest() {
        final String baseuri = endWithSlash(FedoraConfig.getBaseUrl());
        final String acl1 = baseuri + ".acl";
        final String acl2 = baseuri + ".acl/";
        final String acl3 = baseuri + "acls/";
        final String acl4 = baseuri + "acls";
        final String acl5 = baseuri + "acls/foo";
        final String acl6 = baseuri + ".acl/foo";

        final String noacl1 = baseuri + "aclfoo";
        final String noacl2 = baseuri + "fooaclfoo";
        final String noacl3 = baseuri + "fooacl";

        for (final String uri : Arrays.asList(acl1, acl2, acl3, acl4, acl5, acl6)) {
            final State state = new State(0, null, URI.create(uri));
            assertTrue(SKIP_ACLS.test(state));
        }

        for (final String uri : Arrays.asList(noacl1, noacl2, noacl3)) {
            final State state = new State(0, null, URI.create(uri));
            assertFalse(SKIP_ACLS.test(state));
        }
    }

    @Test
    public void skipAclsWithDepthTest() {
        final List<URI> visited = new ArrayList<>();

        final Collection<URI> depth1ExceptAclsAndContainers = union(l2_cows, l2_submissions);

        assertEquals(depth1ExceptAclsAndContainers.size(), toTest.visit(root, visited::add, IGNORE_CONTAINERS,
                depth(2).or(SKIP_ACLS)));
        assertEquals(depth1ExceptAclsAndContainers.size(), visited.size());
        assertTrue(visited.containsAll(depth1ExceptAclsAndContainers));
    }

    private static URI randomUri(URI base) {
        return URI.create(endWithSlash(base.toString() + "/a/b/c/" + UUID.randomUUID().toString()));
    }

    @SafeVarargs
    private static Collection<URI> union(Collection<URI>... collections) {
        final Set<URI> members = new HashSet<>();
        for (final Collection<URI> c : collections) {
            members.addAll(c);
        }

        return members;
    }

}
