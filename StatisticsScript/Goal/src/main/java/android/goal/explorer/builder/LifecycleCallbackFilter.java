package android.goal.explorer.builder;

import soot.SootClass;
import soot.SootMethod;
import soot.jimple.infoflow.android.callbacks.filters.AbstractCallbackFilter;
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants;

import javax.annotation.Nullable;

/**
 * A callback filter that restricts lifecycle callbacks.
 *
 */
public class LifecycleCallbackFilter extends AbstractCallbackFilter {

	private final String applicationClass;

	/**
	 * Creates a new instance of the {@link LifecycleCallbackFilter} class
	 *
	 * @param applicationClass
	 *            The application class of the app
	 */
	public LifecycleCallbackFilter(SootClass applicationClass) {
		this(applicationClass.getName());
	}

	/**
	 * Creates a new instance of the {@link LifecycleCallbackFilter} class
	 *
	 * @param applicationClass
	 *            The class extending android.app.Application
	 */
	public LifecycleCallbackFilter(@Nullable String applicationClass) {
		super();
		this.applicationClass = applicationClass;
		reset();
	}

	@Override
	public boolean accepts(SootClass component, SootClass callbackHandler) {
		return true;
	}

	@Override
	public void reset() {

	}

	@Override
	public boolean accepts(SootClass component, SootMethod callback) {
		// We do not accept ActivityLifecycleCallbacks and ComponentCallbacks in
		// components that are not the application
		if (component.getName().equals(applicationClass))
			return true;
		String subSig = callback.getSubSignature();
		return !AndroidEntryPointConstants.getActivityLifecycleMethods().contains(subSig)
				&& !AndroidEntryPointConstants.getServiceLifecycleMethods().contains(subSig)
				&& !AndroidEntryPointConstants.getFragmentLifecycleMethods().contains(subSig)
				&& !AndroidEntryPointConstants.getBroadcastLifecycleMethods().contains(subSig);
	}

}
