package ecs.util;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.function.Consumer;

public class TypeReflection {

    private static Object JAVA_LANG_ACCESS;
    private static Method GET_CONSTANT_POOL;
    private static Method GET_CONSTANT_POOL_SIZE;
    private static Method GET_CONSTANT_POOL_METHOD_AT;

    static{
        try {
            final Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            Unsafe unsafe = (Unsafe) f.get(null);

            Field implLookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            long implLookupFieldOffset = unsafe.staticFieldOffset(implLookupField);
            Object lookupStaticFieldBase = unsafe.staticFieldBase(implLookupField);
            MethodHandles.Lookup implLookup = (MethodHandles.Lookup) unsafe.getObject(lookupStaticFieldBase, implLookupFieldOffset);
            final MethodHandle overrideSetter = implLookup.findSetter(AccessibleObject.class, "override", boolean.class);
            Consumer<AccessibleObject> makeAccessible = object -> {
                try{
                    overrideSetter.invokeWithArguments(object, true);
                }catch (Throwable e){
                    throw new RuntimeException(e);
                }
            };

            var sharedSecretsClass = Class.forName("jdk.internal.access.SharedSecrets");

            Method javaLangAccessGetter = sharedSecretsClass.getMethod("getJavaLangAccess");
            makeAccessible.accept(javaLangAccessGetter);
            JAVA_LANG_ACCESS = javaLangAccessGetter.invoke(null);
            GET_CONSTANT_POOL = JAVA_LANG_ACCESS.getClass().getMethod("getConstantPool", Class.class);

            Class<?> constantPoolClass = Class.forName("jdk.internal.reflect.ConstantPool");
            GET_CONSTANT_POOL_SIZE = constantPoolClass.getDeclaredMethod("getSize");
            GET_CONSTANT_POOL_METHOD_AT = constantPoolClass.getDeclaredMethod("getMethodAt", int.class);

            makeAccessible.accept(GET_CONSTANT_POOL);
            makeAccessible.accept(GET_CONSTANT_POOL_SIZE);
            makeAccessible.accept(GET_CONSTANT_POOL_METHOD_AT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static Type[] resolveGenericInterfaceArguments(Class<?> implementee, Type genericInterface){
        if (implementee.isSynthetic()){
            try{
                var pool = GET_CONSTANT_POOL.invoke(JAVA_LANG_ACCESS, implementee);
                int num = (Integer)GET_CONSTANT_POOL_SIZE.invoke(pool);
                for(int i = 0; i < num; i ++){
                    Member member;
                    try{
                       member = (Method)GET_CONSTANT_POOL_METHOD_AT.invoke(pool, i);
                    }catch (Exception ignore){
                        member = null;
                    }
                    if (member == null
                            || (member instanceof Constructor
                            && member.getDeclaringClass().getName().equals("java.lang.invoke.SerializedLambda"))
                            || member.getDeclaringClass().isAssignableFrom(implementee))
                        continue;
                    return ((Method) member).getParameterTypes();
                }
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }else{
            return ((ParameterizedType) genericInterface).getActualTypeArguments();
        }
        return null;
    }
}
