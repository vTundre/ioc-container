package entity;

public class DefaultUserService  {
   private MailService mailService;

   public MailService getMailService() {
      return mailService;
   }

   public void setMailService(MailService mailService) {
      this.mailService = mailService;
   }
}
