// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Log
@Interceptor
public class LoggerInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggerInterceptor.class);

	private static final double NANO_TO_MILLISECOND = 1_000_000.0;

	@AroundInvoke
	public Object intercept(final InvocationContext context) throws Exception {

		final Class<?> cls = context.getMethod().getDeclaringClass();

		final StringBuilder message = new StringBuilder();

		final String className = cls.getSimpleName();
		final String methodName = context.getMethod().getName();
		message.append(className).append(".").append(methodName).append(", REQ: ");

		final Object[] parameters = context.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			message.append(CoderDecoder.toJSON(parameters[i]));
			if (i != parameters.length - 1) {
				message.append(",");
			}
		}

		final long startTime = System.nanoTime();
		final Object result = context.proceed();
		final double elapsed = (System.nanoTime() - startTime) / NANO_TO_MILLISECOND;

		if (result instanceof byte[]) {
			message.append(", RES: Binary data");
		} else {
			message.append(", RES: ").append(CoderDecoder.toJSON(result));
		}
		message.append(String.format(" : %.2f ms", elapsed));
		LOGGER.info(message.toString());
		return result;
	}

}
