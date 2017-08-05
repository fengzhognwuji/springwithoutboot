package spring.hk.springcenter.exception;

public class CommonPersistenceException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4571787692051892720L;

	public static final String VERIFIED_ERROR = "验证错误";

	public static final String CAPINNER_CODE_HAS_BEEN_CONSUMED = "该瓶盖码已领取过红包";

	public CommonPersistenceException() {
		super();
	}

	public CommonPersistenceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CommonPersistenceException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommonPersistenceException(String message) {
		super(message);
	}

	public CommonPersistenceException(Throwable cause) {
		super(cause);
	}
}
