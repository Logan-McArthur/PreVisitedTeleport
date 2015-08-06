package com.PromethiaRP.Draeke.PreVisit.Utilities;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import javax.management.ReflectionException;


/**
 * 
 * 
 * No lies, this is not written by me, but I am doing heavy modifications
 */
public class Injector {

	private Set<Provider> providers = new HashSet<Provider>();
	
	
	public void addProvider(Provider pro) {
		providers.add(pro);
	}
	public void inject(Object target) throws RuntimeException {
		try {
			Class<?> clazz = target.getClass();

			if (clazz.isAnnotationPresent(Wire.class)) {
				System.out.println("Annotation present on class declaration.");
				Wire wire = clazz.getAnnotation(Wire.class);
				if (wire != null) {
					injectValidFields(target, clazz, wire.failOnNull(), wire.injectInherited());
				}
			} else {
				injectAnnotatedFields(target, clazz);
			}
		} catch (ReflectionException e) {
			e.printStackTrace();
			throw new RuntimeException("Error while wiring");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Injects the fields that Injector can understand
	 * 
	 * @param target
	 * @param clazz
	 * @param failOnNull
	 * @param injectInherited
	 * @throws ReflectionException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void injectValidFields(Object target, Class<?> clazz, boolean failOnNull, boolean injectInherited)
			throws ReflectionException, IllegalArgumentException, IllegalAccessException {

		Field[] declaredFields = clazz.getDeclaredFields();
		for (int i = 0, s = declaredFields.length; s > i; i++) {
			injectField(target, declaredFields[i], failOnNull);
		}

		// should bail earlier, but it's just one more round.
		while (injectInherited && (clazz = clazz.getSuperclass()) != Object.class) {
			injectValidFields(target, clazz, failOnNull, injectInherited);
		}
	}

	/**
	 * Injects individual fields that have been annotated specifically
	 * 
	 * @param target
	 * @param clazz
	 * @throws ReflectionException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void injectAnnotatedFields(Object target, Class<?> clazz)
		throws ReflectionException, IllegalArgumentException, IllegalAccessException {

		injectClass(target, clazz);
	}
	
	/**
	 * Injects a single Field
	 * 
	 * @param target
	 * @param field
	 * @param failOnNotInjected
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void injectField(Object target, Field field, boolean failOnNotInjected) throws IllegalArgumentException, IllegalAccessException {

		field.setAccessible(true);

		Class<?> fieldType;
		try {
			fieldType = field.getType();
		} catch (RuntimeException ignore) {
			// Swallow exception caused by missing typedata on gwt platfString.format("Failed to inject %s into %s: %s not registered with world.")orm.
			// @todo Workaround, awaiting junkdog-ification. Silently failing injections might be undesirable for users failing to add systems/components to gwt reflection inclusion config.
			return;
		}
		
		for (Provider prov : providers) {
			Class providerClass = prov.getClass();

			if (providerClass.isAssignableFrom(fieldType)) {

				field.set(target, prov);
				return;
			}
			
			
		}
		throw new RuntimeException("" + field.getName());
	}
	
	/**
	 * Goes through the fields and runs the injectField method on each one that has the annotation
	 * 
	 * @param target
	 * @param clazz
	 * @throws ReflectionException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void injectClass(Object target, Class<?> clazz) throws ReflectionException, IllegalArgumentException, IllegalAccessException {
		Field[] declaredFields = clazz.getDeclaredFields();
		for (int i = 0, s = declaredFields.length; s > i; i++) {
			Field field = declaredFields[i];
			if (field.isAnnotationPresent(Wire.class)) {
				System.out.println("Found an Annotated field.");
				injectField(target, field, field.isAnnotationPresent(Wire.class));
			}
		}
	}
}
