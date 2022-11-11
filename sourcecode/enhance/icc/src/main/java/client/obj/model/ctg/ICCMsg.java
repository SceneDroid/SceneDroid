package main.java.client.obj.model.ctg;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import main.java.analyze.utils.output.PrintUtils;

public class ICCMsg implements Cloneable, Serializable {
	private static final long serialVersionUID = 5L;
	private String source;
	private String destination;
	private String action;
	private Set<String> category = new HashSet<String>();
	private String data;
	private String scheme;
	private String host;
	private String path;
	private String port;
	private String type;
	private Set<String> extra = new HashSet<String>();
	

	public ICCMsg(String source, String destination, String action, Set<String> category, String data, String scheme,
			String host, String path, String port, String type, Set<String> extra) {
		this.source = source;
		this.action = action;
		this.category = category;
		this.data = data;
		this.scheme = scheme;
		this.host = host;
		this.path = path;
		this.port = port;
		this.type = type;
		this.extra = new HashSet<String>(extra);
	}

	public ICCMsg() {
	}

	@Override
	public String toString() {
		String res = "";
		if (source != null && !source.equals(""))
			res += " ##source:" + source;
		if (destination != null && !destination.equals(""))
			res += " ##destination:" + destination;
		if (action != null && !action.equals(""))
			res += " ##action:" + action;
		if (category != null && category.size() > 0)
			res += " ##category:" ;
			if (!PrintUtils.printSet(category).equals(""))
				res += PrintUtils.printSet(category);
		if (data != null && !data.equals(""))
			res += " ##data:" + data;
		if (scheme != null && !scheme.equals(""))
			res += " ##scheme:" + scheme;
		if (host != null && !host.equals(""))
			res += " ##host:" + host;
		if (path != null && !path.equals(""))
			res += " ##path:" + path;
		if (port != null && !port.equals(""))
			res += " ##port:" + port;
		if (type != null && !type.equals(""))
			res += " ##type:" + type;
		if (extra != null && extra.size() > 0)
			res += " ##extra:"; 
			if (!PrintUtils.printSet(extra).equals(""))
				res += PrintUtils.printSet(extra," ");
		if (res.equals(""))
			return res;
		return res;
	}


	@Override
	public int hashCode() {
		return this.toString().length();
	}

	@Override
	public boolean equals(Object obj) {
		return obj.toString().equals(this.toString());
	}

	@Override
	public ICCMsg clone() throws CloneNotSupportedException {
		ICCMsg icc = new ICCMsg(source, destination, action, category, data, scheme, host, path, port, type, extra);
		return icc;
	}


	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Set<String> getCategory() {
		return category;
	}

	public void setCategory(Set<String> category) {
		this.category = category;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Set<String> getExtra() {
		return extra;
	}

	public void setExtra(Set<String> extra) {
		this.extra = extra;
	}

}
