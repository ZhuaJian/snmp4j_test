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

public class getDisk {
	public static void collectDisk() {  
		TransportMapping transport = null ;  
		Snmp snmp = null ;  
		CommunityTarget target;  
		String DISK_OID = "1.3.6.1.2.1.25.2.1.4";  
		String[] oids = {"1.3.6.1.2.1.25.2.3.1.2",  //type 存储单元类型  
		     "1.3.6.1.2.1.25.2.3.1.3",  //descr  
		     "1.3.6.1.2.1.25.2.3.1.4",  //unit 存储单元大小  
		     "1.3.6.1.2.1.25.2.3.1.5",  //size 总存储单元数  
		     "1.3.6.1.2.1.25.2.3.1.6"}; //used 使用存储单元数;  
		try {  
		transport = new DefaultUdpTransportMapping();  
		snmp = new Snmp(transport);//创建snmp  
		snmp.listen();//监听消息  
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
		if(values == null ||!DISK_OID.equals(values[0].getVariable().toString()))   
		continue;  
		int unit = Integer.parseInt(values[2].getVariable().toString());//unit 存储单元大小  
		int totalSize = Integer.parseInt(values[3].getVariable().toString());//size 总存储单元数  
		int usedSize = Integer.parseInt(values[4].getVariable().toString());//used  使用存储单元数  
		System.out.println((values[1].getVariable().toString())+"   磁盘大小："+(long)totalSize*unit/(1024*1024*1024)+"G   磁盘使用率为："+(long)usedSize*100/totalSize+"%   磁盘已使用："+(long)usedSize*unit/(1024*1024*1024)+"G");  
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
		collectDisk();
	}

}
