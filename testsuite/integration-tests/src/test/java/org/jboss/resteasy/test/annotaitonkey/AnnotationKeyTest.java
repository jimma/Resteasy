package org.jboss.resteasy.test.annotaitonkey;

import com.fasterxml.jackson.jaxrs.cfg.AnnotationBundleKey;
import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotationKeyTest {
    @Test
    public void testAnnotationKey() throws NoSuchMethodException {
        ConcurrentHashMap<ClassAnnotationKey, String> map = new ConcurrentHashMap<ClassAnnotationKey, String>();

        Method method = SampleRestApi.class.getMethod("someMethod", String.class);
        //Annotation[] annotations = method.getDeclaredAnnotations();
        Annotation[] annotations1 = method.getParameterAnnotations()[0];
        List<Annotation> lists = new ArrayList<Annotation>();
        Annotation[] annotations2 = method.getParameterAnnotations()[0];
        ClassAnnotationKey key1 = new ClassAnnotationKey(SampleRestApi.class, annotations1);
        ClassAnnotationKey key2 = new ClassAnnotationKey(SampleRestApi.class, annotations2);
        //org.junit.Assert.assertTrue(key1 == key2);
        map.put(key1, "key1");
        Assert.assertEquals("key1", map.get(key2));
    }

    @Test
    public void testAnnotationKeyWihoutArrayKey() throws NoSuchMethodException {
        ConcurrentHashMap<ClassAnnotationKeyWithoutArraysKey, String> map = new ConcurrentHashMap<ClassAnnotationKeyWithoutArraysKey, String>();
        Method method = SampleRestApi.class.getMethod("someMethod", String.class);
        Annotation[] annotations1 = method.getParameterAnnotations()[0];
        Annotation[] annotations2 = method.getParameterAnnotations()[0];
        ClassAnnotationKeyWithoutArraysKey key1 = new ClassAnnotationKeyWithoutArraysKey(SampleRestApi.class, annotations1);
        ClassAnnotationKeyWithoutArraysKey key2 = new ClassAnnotationKeyWithoutArraysKey(SampleRestApi.class, annotations2);
        //org.junit.Assert.assertTrue(key1 == key2);
        map.put(key1, "key1");
        Assert.assertEquals("key1", map.get(key2));
    }

    @Test
    public void testAnnotationKeyWithBundleKey() throws NoSuchMethodException {
        ConcurrentHashMap<AnnotationKeyWIthBundleKey, String> map = new ConcurrentHashMap<AnnotationKeyWIthBundleKey, String>();
        Method method = SampleRestApi.class.getMethod("someMethod", String.class);
        Annotation[] annotations1 = method.getParameterAnnotations()[0];
        Annotation[] annotations2 = method.getParameterAnnotations()[0];
        AnnotationKeyWIthBundleKey key1 = new AnnotationKeyWIthBundleKey(SampleRestApi.class, annotations1);
        AnnotationKeyWIthBundleKey key2 = new AnnotationKeyWIthBundleKey(SampleRestApi.class, annotations2);
        //org.junit.Assert.assertTrue(key1 == key2);
        AnnotationBundleKey key3 = new AnnotationBundleKey(annotations1, Object.class);
        AnnotationBundleKey key4 = new AnnotationBundleKey(annotations1, Object.class);
        Assert.assertEquals(key3, key4);
        map.put(key1, "key1");
        Assert.assertEquals("key1", map.get(key2));
    }

}
