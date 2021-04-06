package org.jboss.resteasy.test.annotaitonkey;

import com.fasterxml.jackson.jaxrs.util.ClassKey;

import java.lang.annotation.Annotation;
import java.util.Arrays;
//ClassAnnotationKey
public class ClassAnnotationKey {
    private AnnotationArrayKey annotations;
    private ClassKey classKey;
    private int hash;

    public ClassAnnotationKey(final Class<?> clazz, final Annotation[] annotations) {
        this.annotations = new AnnotationArrayKey(annotations);
        this.classKey = new ClassKey(clazz);
        hash = this.annotations.hashCode();
        hash = 31 * hash + classKey.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassAnnotationKey that = (ClassAnnotationKey) o;

        if (!annotations.equals(that.annotations)) return false;
        if (!classKey.equals(that.classKey)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    private static class AnnotationArrayKey {
        private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

        private final Annotation[] annotations;
        private final int hash;

        private AnnotationArrayKey(final Annotation[] annotations) {
            if (annotations == null || annotations.length == 0) {
                this.annotations = NO_ANNOTATIONS;
            } else {
                this.annotations = annotations;
            }
            this.hash = calcHash(this.annotations);
        }

        private static int calcHash(Annotation[] annotations) {
            int result = annotations.length;
            result = 31 * result + Arrays.hashCode(annotations);
            return result;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            AnnotationArrayKey that = (AnnotationArrayKey) object;
            return hash == that.hash && java.util.Arrays.equals(annotations, that.annotations);
        }
    }
}