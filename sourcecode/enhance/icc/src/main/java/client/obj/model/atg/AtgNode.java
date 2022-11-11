package main.java.client.obj.model.atg;

public class AtgNode {
	private String name;

	public AtgNode(String name) {
		this.name = name;
	}

	public AtgNode() {
	}

	public String getName() {
		return name;
	}

	public static String getClassName(String name) {
		String className = name.replace("-$$Lambda$", "").split("\\$")[0];
		if (className.length() == 0)
			className = name.replace("-", "_").replace("$", "_");
		return className;
	}

	public String getClassName() {
		return getClassName(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof AtgNode))
			return false;
		AtgNode edge = (AtgNode) obj;
		if (this.name == edge.name) {
			return true;
		}
		return false;
	}
}
