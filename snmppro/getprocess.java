package snmppro;

import java.io.IOException;
import java.util.List;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

public class getprocess {
	public static void collectProcess() {  
		TransportMapping transport = null ;  
		Snmp snmp = null ;  
		CommunityTarget target;  
		String[] oids =  
		{"1.3.6.1.2.1.25.4.2.1.1",  //index  
		            "1.3.6.1.2.1.25.4.2.1.2",  //name  
		            "1.3.6.1.2.1.25.4.2.1.4",  //run path  
		     "1.3.6.1.2.1.25.4.2.1.6",  //type  
		     "1.3.6.1.2.1.25.5.1.1.1",  //cpu  
		     "1.3.6.1.2.1.25.5.1.1.2"}; //memory    
		try {  
		transport = new DefaultUdpTransportMapping();  
		snmp = new Snmp(transport);  
		snmp.listen();  
		target = new CommunityTarget();  
		target.setCommunity(new OctetString("public"));  
		target.setRetries(2);  
		target.setAddress(GenericAddress.parse("udp:127.0.0.1/161"));  
		target.setTimeout(8000);  
		target.setVersion(SnmpConstants.version2c);  
		
		TableUtils utils = new TableUtils(snmp, new DefaultPDUFactory(PDU.GETBULK));// GETNEXT
		// GETBULK
		utils.setMaxNumRowsPerPDU(5); // only for GETBULK, set
		// max-repetitions, default is 10
		OID[] columnOIDs = new OID[] { new OID("1.3.6.1.2.1.2.2.1.1")};  
		
		OID[] columns = new OID[oids.length];  
		for (int i = 0; i < oids.length; i++)  
		columns[i] = new OID(oids[i]);  
		@SuppressWarnings("unchecked")  
		List<TableEvent> list = utils.getTable(target, columns, null, null);  
		if(list.size()==1 && list.get(0).getColumns()==null){  
		System.out.println(" null");  
		}else{  
		for(TableEvent event : list){  
		VariableBinding[] values = event.getColumns();  
		if(values == null) continue;  
		String name = values[1].getVariable().toString();//name  
		String cpu = values[4].getVariable().toString();//cpu  
		String memory = values[5].getVariable().toString();//memory  
		String path = values[2].getVariable().toString();//path  
		System.out.println("name--->"+name+"  cpu--->"+cpu+"  memory--->"+memory+"  path--->"+path);  
		}  
		}  
		} catch(Exception e){  
		e.printStackTrace();  
		}finally{  
		try {  
		if(transport!=null)  
		transport.close();  
		if(snmp!=null)  
		snmp.close();  
		} catch (IOException e) {  
		e.printStackTrace();  
		}  
		}  
		}  
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		collectProcess();
	}

}
