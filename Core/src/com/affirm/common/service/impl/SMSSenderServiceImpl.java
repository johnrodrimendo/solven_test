package com.affirm.common.service.impl;

import com.affirm.common.dao.ServiceLogDAO;
import com.affirm.common.service.SMSSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dev5 on 24/03/17.
 */
@Service("smsSenderService")
public class SMSSenderServiceImpl implements SMSSenderService{

    @Autowired
    ServiceLogDAO serviceLogDAO;

//    private List<PreApprovedInfo> sendPreapprovedSMS(List<PreApprovedInfo> preApprovedInfoList, String message){
//        String messageToSend;
//        try{
//            TwilioRestClient client = new TwilioRestClient(Configuration.TWILIO_ACCOUNT_SID, Configuration.TWILIO_AUTH_TOKEN);
//            Account account = client.getAccount();
//            MessageFactory messageFactory = account.getMessageFactory();
//            for (int i = 0; i < preApprovedInfoList.size(); i++) {
//                try{
//                    System.out.println("Enviando a : " + preApprovedInfoList.get(i).getFirstName() + " " + preApprovedInfoList.get(i).getFirstSurname() + " " + preApprovedInfoList.get(i).getCellphone());
//                    List<NameValuePair> params = new ArrayList<>();
//                    messageToSend = message.replace("%NAME%", preApprovedInfoList.get(i).getFirstName());
//                    System.out.println(messageToSend);
//                    params.add(new BasicNameValuePair("To", "+" + preApprovedInfoList.get(i).getEntity().getCountryId() + preApprovedInfoList.get(i).getCellphone()));
//                    params.add(new BasicNameValuePair("From", Configuration.TWILIO_FROM_NUMBER));
//                    params.add(new BasicNameValuePair("Body", messageToSend));
//                    Message sms = messageFactory.create(params);
//                    System.out.println("Mensaje enviado con exito");
//                    preApprovedInfoList.get(i).setSmsSent(true);
//                }catch (Exception e){
//                    System.out.println("Error al enviar el SMS");
//                    preApprovedInfoList.get(i).setSmsSent(false);
//                }
//            }
//
//            return preApprovedInfoList;
//
//        }catch (Exception e){
//            System.out.println("Error al enviar el SMS");
//            return null;
//        }
//    }
//
//    public void sender(Integer entityId, Integer productId, Integer sysuserId, List<PreApprovedInfo> preApprovedInfoList, String message) throws Exception{
//
//        preApprovedInfoList = sendPreapprovedSMS(preApprovedInfoList, message);
//
//        List<PreApprovedInfo> preApprovedInfoListFinal = new ArrayList<>();
//        if(preApprovedInfoList != null){
//            if(preApprovedInfoList.stream().filter(e->!e.getSmsSent()).collect(Collectors.toList()).size() > preApprovedInfoList.size()*0.1){
//                preApprovedInfoListFinal = sendPreapprovedSMS(preApprovedInfoList.stream().filter(e->!e.getSmsSent()).collect(Collectors.toList()), message);
//            }
//        }
//
//        serviceLogDAO.registerSMSSenderServiceLog(
//                preApprovedInfoListFinal != null ? preApprovedInfoListFinal.stream().filter(e->!e.getSmsSent()).count() : 0,
//                (preApprovedInfoList != null ? preApprovedInfoList.stream().filter(e->e.getSmsSent()).count() : 0) +
//                        (preApprovedInfoListFinal != null ? preApprovedInfoListFinal.stream().filter(e->e.getSmsSent()).count() : 0),
//                entityId,
//                productId,
//                sysuserId);
//    }
//
//    public static void main(String[] args) {
//        String filePath = "/home/dev5/Documentos/Twilio/SMSList.txt";
//        List<User> listUserToSMS = new ArrayList<>();
//        String messageToSend;
//        try{
//            String messageToReplace = readFileSMS(filePath, listUserToSMS);
//            for (User user:listUserToSMS) {
//                TwilioRestClient client = new TwilioRestClient(Configuration.TWILIO_ACCOUNT_SID, Configuration.TWILIO_AUTH_TOKEN);
//                Account account = client.getAccount();
//                MessageFactory messageFactory = account.getMessageFactory();
//                List<NameValuePair> params = new ArrayList<>();
//                System.out.println("Enviando a : " + user.getFullName() + " " + user.getPhoneNumber());
//                messageToSend = messageToReplace.replace("%NAME%", user.getFullName().split(" ")[0]);
//                System.out.println(messageToSend);
//                params.add(new BasicNameValuePair("To", user.getPhoneNumber()));
//                params.add(new BasicNameValuePair("From", Configuration.TWILIO_FROM_NUMBER));
//                params.add(new BasicNameValuePair("Body", messageToSend));
//                Message sms = messageFactory.create(params);
//            }
//
//        }catch (FileNotFoundException e){
//            System.out.println("El archivo " + filePath + " no existe");
//        }catch (Exception e){
//            System.out.println("Error al enviar el SMS");
//        }
//
//    }
//
//    private static String readFileSMS(String pathToSMSFile, List<User> listUserToSMS) throws Exception{
//        User userToSMS = new User();
//        String bodySMS = "";
//        if(pathToSMSFile != null && !pathToSMSFile.isEmpty()){
//            BufferedReader br = new BufferedReader(new FileReader(pathToSMSFile));
//            try {
//                StringBuilder sb = new StringBuilder();
//                String line = br.readLine();
//                boolean primeraVez = true;
//                while (line != null) {
//                    if(!primeraVez){
//                        String[] strSplit = line.split(",");
//                        userToSMS.setFullName(strSplit[0]);
//                        userToSMS.setPhoneNumber(strSplit[1]);
//                        listUserToSMS.add(userToSMS);
//                        line = br.readLine();
//                        userToSMS = new User();
//                    }else{
//                        primeraVez = false;
//                        bodySMS = line;
//                        line = br.readLine();
//                    }
//                }
//            } finally {
//                br.close();
//            }
//        }
//        return bodySMS;
//    }
}
