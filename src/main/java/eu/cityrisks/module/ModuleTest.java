/**
 * 
 */
package eu.cityrisks.module;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import eu.cityrisks.model.CrUser;
import eu.cityrisks.model.Device;
import eu.cityrisks.repository.CrUserRepository;
import eu.cityrisks.repository.DeviceRepository;

/**
 * @author aanagnostopoulos
 *
 */
public class ModuleTest {

	private static Log log = LogFactory.getLog(ModuleTest.class);
	
	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private CrUserRepository crUserRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	
	public ModuleTest() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"**/applicationContext.xml");
		AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext
				.getAutowireCapableBeanFactory();
		autowireCapableBeanFactory.autowireBean(this);
	}
	
	
	public void executeFooModule() {
		
		Iterable<Device> devices = deviceRepository.findAll();
		for (Device device : devices) {
			log.info(device.getBrand()+", "+device.getModel());
		}
		
	}
	
	public void executeFooHQLQuery() {
		List<Device> resultList = entityManager.createQuery("from Device", Device.class).getResultList();
		for (Device device : resultList) {
			log.info(device.getBrand()+", "+device.getModel());
		}
	}

	public void executeFooNativeQuery() {
		List resultList = entityManager.createNativeQuery("select * from device").getResultList();
		for (Object device : resultList) {
			log.info(device.toString());
		}
	}
	//Select all users.by distinct email
	public float executeFooNativeQueryUser() {
		float AllUsers = 0;
		List<CrUser> resultList = entityManager.createNativeQuery("select distinct cr_user.email from cr_user").getResultList();
		for (Object user : resultList) {
			//log.info(user.toString());
			AllUsers = AllUsers + 1;
		}
		return AllUsers;
	}
	public float executeFooNativeQueryUserReturnByBrand(String DeviceBrand) {
		//Select the users(by email) that are associated with the device, the model of the device is given as string from scanner.
		List<CrUser> resultList = entityManager.createNativeQuery("select cr_user.email from cr_user, device, user_device where user_device.user_id = cr_user.user_id and user_device.device_id = device.device_id and device.brand like '%"+DeviceBrand+"%'").getResultList();
		//String returnValue = "";
		float returnValue = 0;
		for (Object user : resultList) {
			//log.info(user.toString());
			returnValue = returnValue + 1;
			//returnValue += user.toString() + " \n";
		}
		return returnValue;
	}
	public float executeFooNativeQueryUserReturnByModel(String DeviceModel) {
		//Select the users(by email) that are associated with the device, the model of the device is given as string from scanner.
		List<CrUser> resultList = entityManager.createNativeQuery("select cr_user.email from cr_user, device, user_device where user_device.user_id = cr_user.user_id and user_device.device_id = device.device_id and device.model like '%"+DeviceModel+"%'").getResultList();
		//String returnValue = "";
		float returnValue = 0;
		for (Object user : resultList) {
			//log.info(user.toString());
			returnValue = returnValue + 1;
			//returnValue += user.toString() + " \n";
		}
		return returnValue;
	}
	@Transactional
	public void createTestUser() {
		CrUser crUser = new CrUser();
		crUser.setBirthday(new Date(76, 3, 19));
		crUser.setFirstname("John");
		crUser.setLastname("Smith");
		
		crUserRepository.save(crUser);
	}

	@Transactional
	public void createTestDevice() {
		
		CrUser crUser = crUserRepository.findOne(UUID.randomUUID());
		Set<CrUser> users = new HashSet<CrUser>();
		users.add(crUser);
		
		Device device = new Device();
		device.setBrand("Samsung");
		device.setModel("Galaxy S7");
		device.setCrUsers(users);
		
		deviceRepository.save(device);
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ModuleTest moduleTest = new ModuleTest();
//		moduleTest.executeFooNativeQueryUser();
		
//		moduleTest.executeFooModule();
//		
//		moduleTest.executeFooHQLQuery();
//		
//		moduleTest.executeFooNativeQuery();
		
//		moduleTest.createTestUser();
		
//		moduleTest.createTestDevice();
	}

}
