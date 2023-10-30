package main.java.client.obj;

import java.util.List;

import main.java.client.obj.unitHnadler.UnitHandler;
import soot.SootMethod;
import soot.Unit;

public interface AnalyzerHelper {

	public abstract List<String> getObjectIdentier();

	public abstract boolean isTopTargetUnit(Unit unit);

	public abstract boolean isWarpperTopTargetUnit(Unit unit);

	public abstract String getTypeofUnit(SootMethod m, Unit unit);

	public abstract UnitHandler getUnitHandler(Unit u);

	public abstract boolean isCreateMethod(Unit unit);

	public abstract boolean isStaticCreateMethod(Unit unit);

	public abstract boolean isReceiveFromParatMethod(Unit unit);

	public abstract boolean isPassOutMethod(Unit u);

	public abstract boolean isReceiveFromRetValue(Unit u);

	public abstract boolean isMyTarget(Unit u);

}
