package org.jasig.ssp.util.importer.job.report;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;

import org.jasig.ssp.util.importer.job.staging.PostgresStagingTableWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;

public class ReportGenerator implements JobExecutionListener {

    @Autowired
    private transient JavaMailSender javaMailSender;  
    
    private String dbInstanceName;
    
    private String emailRecipients;
    
    private String replyTo;
    
    Logger logger = LoggerFactory.getLogger(ReportGenerator.class);
    
    
    @Override
    public void beforeJob(JobExecution jobExecution) {
    }
    
    @Override
    public void afterJob(JobExecution jobExecution) {
        String report = buildReport(jobExecution);
        sendEmail(jobExecution, report);
    }

    private void sendEmail(JobExecution jobExecution, String report) {
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage);
        String[] recipients = emailRecipients.split(",");
        try {
            for (String recipient : recipients) {
                    mimeMessageHelper.addTo(recipient);
                    
                    if ( !StringUtils.isEmpty(replyTo) ) {
                        mimeMessageHelper.setReplyTo(replyTo);
                    }
                    mimeMessageHelper.setSubject("Data Import Report for SSP Instance: "+dbInstanceName+" JobId: "+jobExecution.getJobId());
                    mimeMessageHelper.setText(report);
                    javaMailSender.send(mimeMessage);
                    
            }
        } catch (MessagingException e) {
            logger.error(e.toString());
        };
    }

    @SuppressWarnings("unchecked")
    private String buildReport(JobExecution jobExecution) {
        StringBuffer emailMessage = new StringBuffer();
        String EOL = System.getProperty("line.separator");
        SimpleDateFormat dt = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss"); 
        
        emailMessage.append("Start Time:"+dt.format(jobExecution.getCreateTime())+EOL);
        emailMessage.append("End Time:  "+dt.format(jobExecution.getEndTime())+EOL);
        emailMessage.append("Job Id:    "+ jobExecution.getJobId()+EOL);
        emailMessage.append("Job Status:"+ jobExecution.getExitStatus().getExitCode()+EOL);

        emailMessage.append(EOL).append(EOL); 
        
        emailMessage.append("Job Details: "+EOL);
        Map<String, ReportEntry> report = (Map<String, ReportEntry>) jobExecution.getExecutionContext().get("report");
        Set<Entry<String, ReportEntry>> entrySet = report.entrySet();
        for (Entry<String, ReportEntry> entry : entrySet) {
            emailMessage.append(entry.getValue().toString()+EOL);
        }
        
        emailMessage.append(EOL).append(EOL); 

        emailMessage.append("Errors: "+EOL);
        List<ErrorEntry> errors =(List<ErrorEntry>) jobExecution.getExecutionContext().get("errors");
        for (ErrorEntry errorEntry : errors) {
            emailMessage.append(errorEntry.toString()+EOL);
        }
        System.out.print(emailMessage.toString());
        return emailMessage.toString();
    }

    public String getDbInstanceName() {
        return dbInstanceName;
    }

    public void setDbInstanceName(String dbInstanceName) {
        this.dbInstanceName = dbInstanceName;
    }

    public String getEmailRecipients() {
        return emailRecipients;
    }

    public void setEmailRecipients(String emailRecipients) {
        this.emailRecipients = emailRecipients;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }



}
