import java.rmi.RemoteException;
import java.util.Hashtable;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import com.evermind.server.rmi.OrionRemoteException;
import com.vale.sc.unificacaocomercial.unicom.invokerunicom.incarregamento.services.impl.ejb.InCarregamentoEJB;
import com.vale.sc.unificacaocomercial.unicom.invokerunicom.incarregamento.services.impl.ejb.InCarregamentoHome;


public class enviarXML {

	public static void main(String[] args) {
		String xml="<?xml version='1.0' encoding='ISO-8859-1'?><load:Request xmlns:load='http://www.vale.com/LG/20120001/02/LoadingUnicom'><load:Loading><load:Header><load:OperationType>1</load:OperationType><load:LoadingSeries>106</load:LoadingSeries><load:LoadingNumber>@number@</load:LoadingNumber><load:CreationDate>2014-02-19T09:48:01</load:CreationDate><load:FluxNumber>B1313</load:FluxNumber><load:CalculationWeight>170.7</load:CalculationWeight><load:RealWeight>170.7</load:RealWeight><load:MerchandiseValue>170.7</load:MerchandiseValue><load:Currency>BRL</load:Currency><load:UniqueLoading>S</load:UniqueLoading><load:NumberPaidLinkedLoads>0</load:NumberPaidLinkedLoads><load:NumberNonPaidLinkedLoads>0</load:NumberNonPaidLinkedLoads><load:RemunerationIndicator>R</load:RemunerationIndicator><load:WagonAmount>1</load:WagonAmount><load:MerchandiseDetail>Bandeira Preta</load:MerchandiseDetail></load:Header><load:Wagon><load:Number>2532905</load:Number><load:Owner>PART</load:Owner><load:Type>HFE</load:Type><load:CalculationWeight>170.7</load:CalculationWeight><load:RealWeight>170.7</load:RealWeight><load:TotalWeight>170.7</load:TotalWeight><load:Invoice><load:Series>SE5</load:Series><load:Number>63743734677</load:Number><load:Date>2013-12-04</load:Date><load:WagonWeight>71.7</load:WagonWeight></load:Invoice></load:Wagon><load:Invoice><load:Eletronic><load:Series>SE5</load:Series><load:Number>63743734677</load:Number><load:Date>2013-12-04</load:Date><load:Amount>71.700</load:Amount><load:TotalWeight>71.700</load:TotalWeight><load:Value>71.700</load:Value><load:Key>99920433592520031548550010000054901967692333</load:Key><load:PIN>801</load:PIN></load:Eletronic></load:Invoice></load:Loading></load:Request>";
		Integer number = 222300;
		
		try {
			for (Integer i = number-200; i <number; i++) {
				String string = i.toString();
				enviar(xml.replace("@number@", string));
				System.out.println("Número:"+string);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void enviar(String xml) throws Exception{
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.evermind.server.rmi.RMIInitialContextFactory");
		env.put(Context.SECURITY_PRINCIPAL, "oc4jadmin");
		env.put(Context.SECURITY_CREDENTIALS, "oc4jadmin");
		env.put(Context.PROVIDER_URL, "opmn:ormi://zeno.cit:OC4J_dvt83t/unicom");
		env.put("dedicated.rmicontext", Boolean.toString(false));
		env.put("dedicated.connection", Boolean.toString(false));
		boolean flag = false;
		do {
			flag = send(xml, env);			
		}while(flag == false);
	}

	private static boolean send(String xml, Hashtable<String, String> env)
			throws ClassNotFoundException, CreateException,
			RemoteException {
		Context context;
		try {
			context = new InitialContext(env);
			InCarregamentoHome home = (InCarregamentoHome) context.lookup("InCarregamento");
			InCarregamentoHome narrow = (InCarregamentoHome)PortableRemoteObject.narrow(home, Class.forName("com.vale.sc.unificacaocomercial.unicom.invokerunicom.incarregamento.services.impl.ejb.InCarregamentoHome", false, Thread.currentThread().getContextClassLoader()));
			InCarregamentoEJB ejb = narrow.create();
			String result = ejb.syncFreight(xml, "TESTES");
			System.out.println(result);
			return true;
		} catch (NamingException | OrionRemoteException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
}
