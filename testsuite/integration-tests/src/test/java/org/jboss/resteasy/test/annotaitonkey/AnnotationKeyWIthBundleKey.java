package org.jboss.resteasy.test.annotaitonkey;

import com.fasterxml.jackson.jaxrs.cfg.AnnotationBundleKey;
import com.fasterxml.jackson.jaxrs.util.ClassKey;

import java.lang.annotation.Annotation;

public class AnnotationKeyWIthBundleKey {

    private AnnotationBundleKey annotations;
    private ClassKey classKey;
    private int hash;

    public AnnotationKeyWIthBundleKey(final Class<?> clazz, final Annotation[] annotations) {
        this.annotations = new AnnotationBundleKey(annotations, AnnotationBundleKey.class);
        this.classKey = new ClassKey(clazz);
        hash = this.annotations.hashCode();
        hash = 31 * hash + classKey.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnnotationKeyWIthBundleKey that = (AnnotationKeyWIthBundleKey) o;

        if (!annotations.equals(that.annotations)) return false;
        if (!classKey.equals(that.classKey)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
