/*
 * Copyright 2018 Johns Hopkins University
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

package org.dataconservancy.pass.client.integration;

import static com.openpojo.reflection.impl.PojoClassFactory.enumerateClassesByExtendingType;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.dataconservancy.pass.client.PassClient;
import org.dataconservancy.pass.client.fedora.FedoraPassClient;
import org.dataconservancy.pass.model.Contributor;
import org.dataconservancy.pass.model.PassEntity;

import org.apache.commons.beanutils.BeanUtils;
import org.dataconservancy.pass.model.User;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.openpojo.reflection.PojoClass;

/**
 * Base class for client ITs.
 *
 * @author apb@jhu.edu
 */
public abstract class ClientITBase {

    static {
        if (System.getProperty("pass.fedora.baseurl") == null) {
            System.setProperty("pass.fedora.baseurl", "http://localhost:8080/fcrepo/rest/");
        }
    }

    /** All PassEntity classes from pass-model */
    @SuppressWarnings("unchecked")
    protected static final Collection<Class<? extends PassEntity>> PASS_TYPES = enumerateClassesByExtendingType(
            "org.dataconservancy.pass.model", PassEntity.class, null)
                    .stream()
                    .map(PojoClass::getClazz)
                    .map(cls -> (Class<? extends PassEntity>) cls).collect(Collectors.toList());

    protected final PassClient client = new FedoraPassClient();

    private static final Random randomIndex = new Random();

    /**
     * Normalize the context and ID so entities created locally can be compared with entities from the repository.
     *
     * @param e entity
     * @return Entity with context and ID normalized.
     */
    public static PassEntity normalized(PassEntity e) {
        try {
            final PassEntity cloned = (PassEntity) BeanUtils.cloneBean(e);
            cloned.setContext("");
            cloned.setId(URI.create(""));
            return cloned;
        } catch (final Exception x) {
            throw new RuntimeException(x);
        }
    }

    /**
     * Create an empty PASS entity
     *
     * @param obj Class of pass entity.
     * @return An instantiated, but empty, pass entity.
     */
    public static <T extends PassEntity> T empty(Class<T> obj) {
        try {
            return obj.newInstance();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create an instance of a Pass Entity, filled with random data.
     * <p>
     * Uses reflection to populate the fields with type-appropriate data.
     * </p>
     *
     * @param obj Class of pass entity.
     * @param sizeOfLists For fields that are lists, fill them with this many values.
     * @return Populated pass entity.
     */
    public static <T extends PassEntity> T random(Class<T> obj, int sizeOfLists) {
        try {
            final T entity = obj.newInstance();

            for (final Method m : obj.getMethods()) {
                if (m.getName().startsWith("set") && !m.getName().equals("setId")) {
                    final Class<?> type = m.getParameterTypes()[0];

                    if (String.class.isAssignableFrom(type)) {
                        m.invoke(entity, UUID.randomUUID().toString());
                    } else if (Enum.class.isAssignableFrom(type)) {
                        final Object[] constants = type.getEnumConstants();
                        m.invoke(entity, constants[randomIndex.nextInt(constants.length)]);
                    } else if (URI.class.isAssignableFrom(type)) {
                        m.invoke(entity, URI.create("urn:uuid:" + UUID.randomUUID()));
                    } else if (DateTime.class.isAssignableFrom(type)) {
                        m.invoke(entity, new DateTime(randomIndex.nextInt(Integer.MAX_VALUE)).withZone(
                                DateTimeZone.UTC));
                    } else if (List.class.isAssignableFrom(type)) {
                        m.invoke(entity, createList(m, sizeOfLists));
                    } else if (Boolean.class.isAssignableFrom(type)) {
                        m.invoke(entity, true);
                    } else {
                        throw new UnsupportedOperationException(type.getName());
                    }
                }
            }

            return entity;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* We can't infer types of lists due to erasure. so we cheat here. */
    @SuppressWarnings("unchecked")
    private static List<?> createList(Method method, int size) throws Exception {
        @SuppressWarnings("rawtypes")
        final List list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (method.getName().equals("setIssns") || method.getName().equals("setExternalIds")) {
                list.add(UUID.randomUUID().toString());
            } else if (method.getName().equals("setRoles")) {
                Object role = method.getDeclaringClass().newInstance();
                if (role instanceof User) {
                    User.Role[] values = User.Role.values();
                    list.add(values[i]); 
                } else if (role instanceof Contributor) {
                    Contributor.Role[] values = Contributor.Role.values();
                    list.add(values[i]);      
                }
            } else {
                list.add(URI.create("urn:uuid:" + UUID.randomUUID().toString()));
            }
        }

        return list;
    }

}
