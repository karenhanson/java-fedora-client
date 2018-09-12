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

import static java.util.Collections.emptyList;

import java.net.URI;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Crawl/walk through a hierarchy of containers in a repository.
 * <p>
 * Given a URI of an LDP container, this class will visit all its children, their childrens children, etc up to a
 * provided depth and invoke given {@link Consumer}. It is designed to handle an arbitrary large number of resources.
 * </p>
 *
 * @author apb@jhu.edu
 */
public class RepositoryCrawler {

    Lister repo = new FcrepoLister();

    /**
     * Visit a container and its children.
     * <p>
     * Invokes a supplied visitor {@link consumer} for every resource underneath a repository container, potentially
     * ignoring individual resources, or skipping trees or limiting recursion depth respectively.
     * </p>
     * <p>
     * Some examples:
     * <ul>
     * <li>Process all submissions, but <em>not</em> binaries POSTed to submissions, and <em>not</em> the submissions
     * container itself:
     * <code>visit("https://pass.local/fcepo/rest/submissions/", consumer, IGNORE_ROOT, depth(1));</code></li>
     * <li>Process all PASS resources, skipping ACLs and top-level containers:
     * <code>visit("https://pass.local/fcepo/rest/", consumer, IGNORE_CONTAINERS, depth(2).or(SKIP_ACLS));</code>
     * </ul>
     * </p>
     *
     * @param resource URI of a resource to visit. If the value ends in <code>/*</code>, the crawler will <em>not</em>
     *        consume the provided container itself, but <em>will</em> consume its children.
     * @param visitor For every resource visited, it will invoke the consumer with the URI of the current resource.
     * @param ignore Predicate which, when true, will cause a given resource to be ignored; it will not be sent to the
     *        visitor, but recursion will still continue to its children. A primary use case is ignoring a container
     *        resource, but processing its children. See also {@link Ignore}. To ignore none, use
     *        {@link Ignore#IGNORE_NONE}. If combining multiple predicates, use "or" (i.e. "if one says ignore,
     *        ignore").
     * @param skip Predicate which, when true, tells the crawler to stop crawling at that resource, and not visit its
     *        children. To process every resource without stopping at any, use {@link Skip#SKIP_NONE}. See also
     *        {@link Skip}. If combining multiple predicates, use "or" (i.e. "if one says stop, stop").
     * @return the number of resources visited.
     */
    public int visit(final URI resource, final Consumer<URI> visitor, Predicate<State> ignore,
            Predicate<State> skip) {
        return _visit(resource, visitor, new State(0, null, resource), ignore, skip);
    }

    private int _visit(final URI resource, final Consumer<URI> visitor, State state, Predicate<State> ignore,
            Predicate<State> terminal) {
        int count = 0;

        final Collection<URI> children;

        if (!ignore.test(state)) {
            // We're not ignoring the resource

            if (!terminal.test(state)) {
                // If it's not terminal, get its children.
                children = repo.getChildren(resource);
            } else {
                // If it is terminal, do not get its children
                children = emptyList();
            }

            // Increment counter and visit.
            count++;
            visitor.accept(resource);
        } else if (!terminal.test(state)) {
            // Ignored, but not terminal. Get its children.
            children = repo.getChildren(resource);
        } else {
            // Ignored and terminal. Do not visit children,
            children = emptyList();
        }

        // Visit children.
        for (final URI child : children) {
            count += _visit(child, visitor, new State(state.depth + 1, resource, child), ignore, terminal);
        }

        return count;
    }

    /**
     * Represents repository crawling state.
     *
     * @author apb@jhu.edu
     */
    public class State {

        /** The depth of recursion */
        public final int depth;

        /** The parent resource, null for the root of a tree */
        public final URI parent;

        /** URI of the current resource. */
        public final URI id;

        /**
         * Create an immutible crawling state.
         *
         * @param depth The depth of recursion
         * @param parent The parent resource, null for the root of a tree
         * @param id URI of the current resource.
         */
        public State(int depth, URI parent, URI id) {
            this.depth = depth;
            this.parent = parent;
            this.id = id;
        }
    }

    /**
     * Useful predicates for indicating resources to skip when crawling.
     *
     * @author apb@jhu.edu
     */
    public static interface Skip {

        /** Do not skip any resources */
        public static final Predicate<State> SKIP_NONE = s -> false;

        /** Skip ACLs */
        public static final Predicate<State> SKIP_ACLS = s -> s.id.toString().matches(".+/acls[/.+?|$]");

        /**
         * Limit recursion to a given depth.
         *
         * @param limit Recursion limit. 0 = no recursion, 1 = descent to children, 2 = descend to the children's
         *        children, etc.
         * @return
         */
        public static Predicate<State> depth(int limit) {
            return s -> s.depth >= limit;
        }
    }

    /**
     * Useful predicates for indicating resources to ignore when crawling.
     *
     * @author apb@jhu.edu
     */
    public static interface Ignore {

        /** Do not ignore any resources */
        public static final Predicate<State> IGNORE_NONE = s -> false;

        /** Ignore the given container (root of a tree being traversed) */
        public static final Predicate<State> IGNORE_ROOT = s -> s.parent == null;

        /** Ignore all "top level" containers for PASS entities, such as /submissions, etc */
        public static final Predicate<State> IGNORE_CONTAINERS = s -> s.id.toString().matches(
                endWithSlash(FedoraConfig.getBaseUrl()) + "[a-zA-Z]+/*$") ||
                RepositoryCrawler.endWithSlash(s.id.toString()).equals(
                        RepositoryCrawler.endWithSlash(FedoraConfig.getBaseUrl()));
    }

    static String endWithSlash(String uri) {
        if (uri.endsWith("/")) {
            return uri;
        } else {
            return uri + "/";
        }
    }
}
