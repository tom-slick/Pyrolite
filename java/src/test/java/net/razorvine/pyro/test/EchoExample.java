package net.razorvine.pyro.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import net.razorvine.pickle.PrettyPrint;
import net.razorvine.pyro.Config;
import net.razorvine.pyro.NameServerProxy;
import net.razorvine.pyro.PyroException;
import net.razorvine.pyro.PyroProxy;

/**
 * Simple example that shows the use of Pyro with the Pyro echo server.
 *  
 * @author Irmen de Jong (irmen@razorvine.net)
 */
public class EchoExample {

	static protected byte[] hmacKey;	// just ignore this if you don't specify a PYRO_HMAC_KEY environment var

	public static void main(String[] args) throws IOException {

		System.out.println("Testing Pyro echo server (make sure it's running, with nameserver enabled)...");
		System.out.println("Pyrolite version: "+Config.PYROLITE_VERSION);

		setConfig();

		NameServerProxy ns = NameServerProxy.locateNS(null);
		PyroProxy p = new PyroProxy(ns.lookup("test.echoserver"));
		p.pyroHmacKey = hmacKey;
		ns.close();
		
		// PyroProxy p=new PyroProxy("localhost",9999,"test.echoserver");

		Object x=42;
		System.out.println("echo param:");
		PrettyPrint.print(x);
		Object result=p.call("echo", x);
		System.out.println("return value:");
		PrettyPrint.print(result);
		
		String s="This string is way too long. This string is way too long. This string is way too long. This string is way too long. ";
		s=s+s+s+s+s;
		System.out.println("echo param:");
		PrettyPrint.print(s);
		result=p.call("echo", s);
		System.out.println("return value:");
		PrettyPrint.print(result);

		System.out.println("error test.");
		try {
			result=p.call("error");
		} catch (PyroException e) {
			System.out.println("Pyro Exception (expected)! "+e.getMessage());
			System.out.println("Pyro Exception cause: "+e.getCause());
			System.out.println("Pyro Exception remote traceback:\n>>>\n"+e._pyroTraceback+"<<<");
		}

		System.out.println("shutting down the test echo server.");
		p.call("shutdown");
		
		// tidy up:
		p.close();
	}
	
	static void setConfig() {
		String hmackey=System.getenv("PYRO_HMAC_KEY");
		String hmackey_property=System.getProperty("PYRO_HMAC_KEY");
		if(hmackey_property!=null) {
			hmackey=hmackey_property;
		}
		if(hmackey!=null && hmackey.length()>0) {
			try {
				hmacKey=hmackey.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				hmacKey=null;
			}
		}
		String tracedir=System.getenv("PYRO_TRACE_DIR");
		if(System.getProperty("PYRO_TRACE_DIR")!=null) {
			tracedir=System.getProperty("PYRO_TRACE_DIR");
		}
		
		String serializer=System.getenv("PYRO_SERIALIZER");
		if(System.getProperty("PYRO_SERIALIZER")!=null) {
			serializer=System.getProperty("PYRO_SERIALIZER");
		}
		if(serializer!=null) {
			Config.SERIALIZER = Enum.valueOf(Config.SerializerType.class, serializer);
		}

		Config.MSG_TRACE_DIR=tracedir;
	}	
}
