/**
 * 
 */
package no.naks.rammeverk.kildelag.dao;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import no.naks.rammeverk.kildelag.model.MailReceiver;
import no.naks.rammeverk.mailer.Mailer;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Denne klassen setter opp dagens aktiviteter av
 * utsendelser av epost til gitt tidspunkter.
 * Det blir laget en instans av denne klassen for hvert utsendelsestidspunkt.
 * @author qaa
 *
 */
public class EmailScheduler  {
	private Mailer mailReminder;
	
	public EmailScheduler() {
	}
	
	public EmailScheduler(Mailer mailReminder) {
		super();
		this.mailReminder = mailReminder;
	}

	protected void startService(Mailer mailSender,String language, String type, String setSchedule, String jobName)throws SchedulerException, ParseException {
		Log log = LogFactory.getLog(EmailScheduler.class);
		Date date = new Date();
		JobKey jobKey = new JobKey(jobName, jobName);
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		JobDetail jobDetail = new JobDetailImpl();
		JobDetail job = new JobDetailImpl();
		Trigger trigger= new CronTriggerImpl() ;
		job =scheduler.getJobDetail(jobKey);
		if(job!=null){
		    if(job.getKey().getName().equals(jobName)){
				scheduler.deleteJob(jobKey);
				log.info(jobName + " is deleted");
		    }	
		}

		/*
		 * jobDetail.setName(jobName); jobDetail.setGroup(jobName);
		 * trigger.setName(jobName); jobDetail.setJobClass(EmailReportJob.class); //
		 * Parametriseres !!!
		 */	
		if (mailSender != null)
			jobDetail.getJobDataMap().put("mailSender",mailSender);
		jobDetail.getJobDataMap().put("language", language);
		jobDetail.getJobDataMap().put("type", type);
		if (mailReminder != null)
			jobDetail.getJobDataMap().put("mailReminder",mailReminder);

//		((CronTrigger) trigger).setCronExpression(setSchedule);
		scheduler.scheduleJob(jobDetail,trigger);
	//	List jobber = scheduler.getCurrentlyExecutingJobs();
		log.info(jobName + " email scheduler time is set at "+setSchedule);
	}
}
