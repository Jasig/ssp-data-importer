/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.ssp.util.importer.job.report;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jasig.ssp.util.importer.job.tasklet.PartialUploadGuardException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;

public class ReportGenerator implements JobExecutionListener {

    private transient JavaMailSender javaMailSender;

    private String batchTitle;

    private String emailRecipients;

    private String replyTo;

    private boolean sendEmail;

    private boolean emailReport = false;

    Logger logger = LoggerFactory.getLogger(ReportGenerator.class);


    @Override
    public void beforeJob(JobExecution jobExecution) {

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        emailReport = false;
        String report = buildReport(jobExecution);
        if(sendEmail  && emailReport)
        {
            sendEmail(jobExecution, report);
        }
    }

    private void sendEmail(JobExecution jobExecution, String report) {

        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage);
        String[] recipients = emailRecipients.split(",");
        String EOL = System.getProperty("line.separator");

        try {
            for (String recipient : recipients) {
                    mimeMessageHelper.addTo(recipient);

                    if ( !StringUtils.isEmpty(replyTo) ) {
                        mimeMessageHelper.setReplyTo(replyTo);
                    }
                    mimeMessageHelper.setSubject("Data Import Report for SSP Instance: "+batchTitle+" JobId: "+jobExecution.getJobId());
                    mimeMessageHelper.setText(report);
                    javaMailSender.send(mimeMessage);

            }
            logger.info("Report emailed" + EOL);
        } catch (MessagingException e) {
            logger.error(e.toString());
        };
    }

    @SuppressWarnings("unchecked")
    private String buildReport(JobExecution jobExecution) {
        StringBuffer emailMessage = new StringBuffer();
        String EOL = System.getProperty("line.separator");
        SimpleDateFormat dt = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        long diff = jobExecution.getEndTime().getTime() - jobExecution.getCreateTime().getTime();//as given

        emailMessage.append("Start Time:    "+dt.format(jobExecution.getCreateTime())+EOL);
        emailMessage.append("End Time:      "+dt.format(jobExecution.getEndTime())+EOL);
        emailMessage.append("Duration:      "+ DurationFormatUtils.formatDurationWords(diff, true, true)
                + " ("+ DurationFormatUtils.formatDurationHMS(diff)+ ")" +EOL);
        emailMessage.append("Job Id:        "+ jobExecution.getJobId()+EOL);
        emailMessage.append("Job Paramters: "+ jobExecution.getJobParameters()+EOL);
        emailMessage.append("Job Status:    "+ jobExecution.getExitStatus().getExitCode()+EOL);

        emailMessage.append(EOL).append(EOL);

        emailMessage.append("Job Details: "+EOL);
        Map<String, ReportEntry> report = (Map<String, ReportEntry>) jobExecution.getExecutionContext().get("report");
        if(report != null){
            Set<Entry<String, ReportEntry>> entrySet = report.entrySet();
            for (Entry<String, ReportEntry> entry : entrySet) {
                emailMessage.append(entry.getValue().toString()+EOL);
            }
            if(entrySet.size() > 0)
                emailReport = true;
        }else{
            emailMessage.append("NO FILES PROCESSED." + EOL);
        }

        emailMessage.append(EOL).append(EOL);

        emailMessage.append("Errors: "+EOL);
        List<ErrorEntry> errors =(List<ErrorEntry>) jobExecution.getExecutionContext().get("errors");
        List<Throwable> failureExceptions = jobExecution.getAllFailureExceptions();
        if(errors != null)
        {
            for (ErrorEntry errorEntry : errors) {
                emailMessage.append(errorEntry.toString()+EOL);
                emailMessage.append(EOL);
            }
        }else if(failureExceptions == null || failureExceptions.size()==0){
            emailMessage.append("No Errors Found." + EOL);
        }

        if(failureExceptions != null){
            for(Throwable failureException:failureExceptions){
                 if(ExceptionUtils.indexOfThrowable(failureException, PartialUploadGuardException.class) >= 0 ||
                         ExceptionUtils.indexOfThrowable(failureException, BeanCreationException.class) >= 0){
                     emailReport = true;
                     logger.info("emailReport:" + emailReport);
                 }
                logger.info("failureException:" + failureException.getClass().getName());
                emailMessage.append(failureException.getMessage() + EOL);
            }
        }
        
        String validations = (String)jobExecution.getExecutionContext().get("databaseValidations");
        if(validations != null){
        	emailMessage.append("Database Validations:"  + EOL + validations);
        }

        logger.info(emailMessage.toString());
        return emailMessage.toString();
    }

    public String getBatchTitle() {
        return batchTitle;
    }

    public void setBatchTitle(String batchTitle) {
        this.batchTitle = batchTitle;
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

    public boolean isSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public void setJavaMailSender(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

}
