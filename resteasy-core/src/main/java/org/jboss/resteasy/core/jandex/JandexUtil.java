package org.jboss.resteasy.core.jandex;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
public class JandexUtil
{
    /**
     * @see {@link Index#getKnownDirectSubclasses(org.jboss.jandex.DotName)}
     */
    public static Set<ClassInfo> getKnownDirectSubclasses(Collection<Index> indexes,final DotName className) {
        final Set<ClassInfo> allKnown = new HashSet<ClassInfo>();
        for (Index index : indexes) {
            final List<ClassInfo> list = index.getKnownDirectSubclasses(className);
            if (list != null) {
                allKnown.addAll(list);
            }
        }
        return Collections.unmodifiableSet(allKnown);
    }

    public static Set<ClassInfo> getAllKnownSubclasses(Collection<Index> indexes,final DotName className) {
        final Set<ClassInfo> allKnown = new HashSet<ClassInfo>();
        final Set<DotName> processedClasses = new HashSet<DotName>();
        getAllKnownSubClasses(indexes,className, allKnown, processedClasses);
        return allKnown;
    }

    private static void getAllKnownSubClasses(Collection<Index> indexes,DotName className, Set<ClassInfo> allKnown, Set<DotName> processedClasses) {
        final Set<DotName> subClassesToProcess = new HashSet<DotName>();
        subClassesToProcess.add(className);
        while (!subClassesToProcess.isEmpty()) {
            final Iterator<DotName> toProcess = subClassesToProcess.iterator();
            DotName name = toProcess.next();
            toProcess.remove();
            processedClasses.add(name);
            getAllKnownSubClasses(indexes, name, allKnown, subClassesToProcess, processedClasses);
        }
    }

    private static void getAllKnownSubClasses(Collection<Index> indexes,DotName name, Set<ClassInfo> allKnown, Set<DotName> subClassesToProcess,
                                       Set<DotName> processedClasses) {
        for (Index index : indexes) {
            final List<ClassInfo> list = index.getKnownDirectSubclasses(name);
            if (list != null) {
                for (final ClassInfo clazz : list) {
                    final DotName className = clazz.name();
                    if (!processedClasses.contains(className)) {
                        allKnown.add(clazz);
                        subClassesToProcess.add(className);
                    }
                }
            }
        }
    }

    public static Set<ClassInfo> getKnownDirectImplementors(Collection<Index> indexes,final DotName className) {
        final Set<ClassInfo> allKnown = new HashSet<ClassInfo>();
        for (Index index : indexes) {
            final List<ClassInfo> list = index.getKnownDirectImplementors(className);
            if (list != null) {
                allKnown.addAll(list);
            }
        }
        return Collections.unmodifiableSet(allKnown);
    }

    public static Set<ClassInfo> getAllKnownImplementors(Collection<Index> indexes, final DotName interfaceName) {
        final Set<ClassInfo> allKnown = new HashSet<ClassInfo>();
        final Set<DotName> subInterfacesToProcess = new HashSet<DotName>();
        final Set<DotName> processedClasses = new HashSet<DotName>();
        subInterfacesToProcess.add(interfaceName);
        while (!subInterfacesToProcess.isEmpty()) {
            final Iterator<DotName> toProcess = subInterfacesToProcess.iterator();
            DotName name = toProcess.next();
            toProcess.remove();
            processedClasses.add(name);
            getKnownImplementors(indexes, name, allKnown, subInterfacesToProcess, processedClasses);
        }
        return allKnown;
    }

    private static void getKnownImplementors(Collection<Index> indexes,DotName name, Set<ClassInfo> allKnown, Set<DotName> subInterfacesToProcess,
                                      Set<DotName> processedClasses) {
        for (Index index : indexes) {
            final List<ClassInfo> list = index.getKnownDirectImplementors(name);
            if (list != null) {
                for (final ClassInfo clazz : list) {
                    final DotName className = clazz.name();
                    if (!processedClasses.contains(className)) {
                        if (Modifier.isInterface(clazz.flags())) {
                            subInterfacesToProcess.add(className);
                        } else {
                            if (!allKnown.contains(clazz)) {
                                allKnown.add(clazz);
                                processedClasses.add(className);
                                getAllKnownSubClasses(indexes, className, allKnown, processedClasses);
                            }
                        }
                    }
                }
            }
        }
    }

    public static ClassInfo getClassByName(Collection<Index> indexes, final DotName className) {
        for (Index index : indexes) {
            final ClassInfo info = index.getClassByName(className);
            if (info != null) {
                return info;
            }
        }
        return null;
    }

    public static  Collection<ClassInfo> getKnownClasses(Collection<Index> indexes) {
        final List<ClassInfo> allKnown = new ArrayList<ClassInfo>();
        for (Index index : indexes) {
            final Collection<ClassInfo> list = index.getKnownClasses();
            if (list != null) {
                allKnown.addAll(list);
            }
        }
        return Collections.unmodifiableCollection(allKnown);
    }

    public static List<AnnotationInstance> getAnnotations(Collection<Index> indexes, final DotName annotationName) {
        final List<AnnotationInstance> allInstances = new ArrayList<AnnotationInstance>();
        for (Index index : indexes) {
            final List<AnnotationInstance> list = index.getAnnotations(annotationName);
            if (list != null) {
                allInstances.addAll(list);
            }
        }
        return Collections.unmodifiableList(allInstances);
    }
}
