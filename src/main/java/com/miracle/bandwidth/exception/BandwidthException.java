package com.miracle.bandwidth.exception;

import com.miracle.cognitive.exception.CognitiveErrorCodes;
import com.miracle.cognitive.exception.CognitiveServiceException;

public class BandwidthException extends CognitiveServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BandwidthException(Throwable cause, String errorCode, String errorDescription) {
		super(cause, errorCode, errorDescription);
		if (getErrorCode().equalsIgnoreCase(CognitiveErrorCodes.UNKOWN_ERROR)) {
			setErrorCode(getBandiwdthErrorCode(cause));
		}
	}

	public BandwidthException(Throwable cause, String errorDescription) {
		super(cause, errorDescription);
		if (getErrorCode().equalsIgnoreCase(CognitiveErrorCodes.UNKOWN_ERROR)) {
			setErrorCode(getBandiwdthErrorCode(cause));
		}
	}

	public String getBandiwdthErrorCode(Throwable cause) {
		String errorCode = null;
		// Check exception instance
		return errorCode;
	}
}
