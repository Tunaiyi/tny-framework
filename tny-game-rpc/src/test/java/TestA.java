import java.lang.reflect.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/2 5:27 下午
 */
public class TestA {

	private static Class<?> bodyGenericType(Method method) {
		Type type = method.getGenericReturnType();
		if (type instanceof Class) {
			return (Class<?>)type;
		}
		if (type instanceof ParameterizedType) {
			Type[] actualTypeValue = ((ParameterizedType)type).getActualTypeArguments();
			Type typeClass = actualTypeValue[0];
			if (typeClass instanceof ParameterizedType) {
				ParameterizedType bodyType = (ParameterizedType)typeClass;
				return (Class<?>)bodyType.getRawType();
			}
			return (Class<?>)typeClass;
		}
		throw new IllegalArgumentException();
	}

	public static void main(String[] args) {

		for (Method method : TInterface.class.getMethods()) {
			System.out.println(bodyGenericType(method));
		}
	}

}
