package org.jasig.ssp.util.importer.job.report;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.mail.internet.MimeMessage;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

public class ReportGenerator implements StepExecutionListener{



    @Override
    public void beforeStep(StepExecution stepExecution) {
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        StringBuffer emailMessage = new StringBuffer();
        String EOL = System.getProperty("line.separator");
        Map<String, ReportEntry> report = (Map<String, ReportEntry>) stepExecution.getJobExecution().getExecutionContext().get("report");
        Set<Entry<String, ReportEntry>> entrySet = report.entrySet();
        for (Entry<String, ReportEntry> entry : entrySet) {
            emailMessage.append(entry.getValue().toString()+EOL);
        }
        emailMessage.append("Errors: "+EOL);
        List<ErrorEntry> errors =(List<ErrorEntry>) stepExecution.getJobExecution().getExecutionContext().get("errors");
        if(errors != null){
            for (ErrorEntry errorEntry : errors) {
                emailMessage.append(errorEntry.toString()+EOL);
            }
        }
        System.out.print(emailMessage.toString());

        return ExitStatus.COMPLETED;
    }

}
