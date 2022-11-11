package main.java.client.soot;

import java.util.Map;

import soot.Body;
import soot.BodyTransformer;

public class ActiveBodyTransformer extends BodyTransformer {

	@Override
	/**
	 * must override
	 */
	protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
		b.getMethod().setActiveBody(b);
	}
}