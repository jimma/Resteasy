package org.jboss.resteasy.test.annotaitonkey;

import com.fasterxml.jackson.jaxrs.util.ClassKey;

import java.lang.annotation.Annotation;
import java.util.Arrays;

public class ClassAnnotationKeyWithoutArraysKey {

    private Annotation[] annotations;
    private ClassKey classKey;
    private int hash;

    public ClassAnnotationKeyWithoutArraysKey(final Class<?> clazz, final Annotation[] annotations) {
        this.classKey = new ClassKey(clazz);
        this.annotations = annotations;
        hash = Arrays.hashCode(this.annotations) + classKey.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassAnnotationKeyWithoutArraysKey that = (ClassAnnotationKeyWithoutArraysKey) o;
        if (!classKey.equals(that.classKey)) return false;
        //if method annotations the same
        if (annotations != null && annotations.equals(that.annotations)) {
            return true;
        }
        return Arrays.equals(annotations, that.annotations);
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
