package com.can.buyerApp.mapper;

import com.can.buyerApp.dto.Acknowledgement;
import com.can.buyerApp.request.*;
import org.springframework.stereotype.Component;

@Component
public class RequestMapper {


    public Acknowledgement searchAckResponse(MotorOnSearchRequest response) {

        Acknowledgement responsee = new Acknowledgement();

        Acknowledgement.Context context = new Acknowledgement.Context();
        context.setAction(response.getContext().getAction());
        context.setBap_id(response.getContext().getBap_id());
        context.setBap_uri(response.getContext().getBap_uri());
        context.setDomain(response.getContext().getDomain());
        context.setMessage_id(response.getContext().getMessage_id());
        context.setTransaction_id(response.getContext().getTransaction_id());
        context.setTtl(response.getContext().getTtl());
        Acknowledgement.Context.Location location = new Acknowledgement.Context.Location();
        location.setCity(new Acknowledgement.Context.Location.City(response.getContext().getLocation().getCity().getCode()));
        location.setCountry(new Acknowledgement.Context.Location.Country(response.getContext().getLocation().getCountry().getCode()));
        context.setLocation(location);
        context.setVersion(response.getContext().getVersion());
        Acknowledgement.Message message = new Acknowledgement.Message();
        message.setAck(new Acknowledgement.Message.Ack(AckStatus.ACK));
        responsee.setContext(context);
        responsee.setMessage(message);
        return responsee;
    }

    public Acknowledgement selectAckResponse(MotorOnselectRequest selectRequest) {

        Acknowledgement response = new Acknowledgement();
        Acknowledgement.Context context = new Acknowledgement.Context();
        context.setAction(selectRequest.getContext().getAction());
        context.setBap_id(selectRequest.getContext().getBap_id());
        String bapUri = selectRequest.getContext().getBap_uri();
        context.setBap_uri(bapUri );
        context.setDomain(selectRequest.getContext().getDomain());
        context.setMessage_id(selectRequest.getContext().getMessage_id());
        context.setTransaction_id(selectRequest.getContext().getTransaction_id());
        context.setTtl(selectRequest.getContext().getTtl());
        Acknowledgement.Context.Location location = new Acknowledgement.Context.Location();
        location.setCity(new Acknowledgement.Context.Location.City(selectRequest.getContext().getLocation().getCity().getCode()));
        location.setCountry(new Acknowledgement.Context.Location.Country(selectRequest.getContext().getLocation().getCountry().getCode()));
        context.setLocation(location);
        context.setVersion(selectRequest.getContext().getVersion());
        Acknowledgement.Message message = new Acknowledgement.Message();
        message.setAck(new Acknowledgement.Message.Ack(AckStatus.ACK));
        response.setContext(context);
        response.setMessage(message);
        return response;
    }

    public Acknowledgement selectAckResponse(OnselectRequest selectRequest) {

        Acknowledgement response = new Acknowledgement();
        Acknowledgement.Context context = new Acknowledgement.Context();
        context.setAction(selectRequest.getContext().getAction());
        context.setBap_id(selectRequest.getContext().getBap_id());
        String bapUri = selectRequest.getContext().getBap_uri();
        context.setBap_uri(bapUri );
        context.setDomain(selectRequest.getContext().getDomain());
        context.setMessage_id(selectRequest.getContext().getMessage_id());
        context.setTransaction_id(selectRequest.getContext().getTransaction_id());
        context.setTtl(selectRequest.getContext().getTtl());
        Acknowledgement.Context.Location location = new Acknowledgement.Context.Location();
        location.setCity(new Acknowledgement.Context.Location.City(selectRequest.getContext().getLocation().getCity().getCode()));
        location.setCountry(new Acknowledgement.Context.Location.Country(selectRequest.getContext().getLocation().getCountry().getCode()));
        context.setLocation(location);
        context.setVersion(selectRequest.getContext().getVersion());
        Acknowledgement.Message message = new Acknowledgement.Message();
        message.setAck(new Acknowledgement.Message.Ack(AckStatus.ACK));
        response.setContext(context);
        response.setMessage(message);
        return response;
    }

    public  Acknowledgement initAckResponse(OnInitRequest initRequest) {

        Acknowledgement response = new Acknowledgement();
        Acknowledgement.Context context = new Acknowledgement.Context();
        context.setAction(initRequest.getContext().getAction());
        context.setBap_id(initRequest.getContext().getBap_id());
        context.setBap_uri(initRequest.getContext().getBap_uri());
        context.setDomain(initRequest.getContext().getDomain());
        context.setMessage_id(initRequest.getContext().getMessage_id());
        context.setTimestamp(java.time.Instant.now().toString());
        context.setTransaction_id(initRequest.getContext().getTransaction_id());
        context.setTtl(initRequest.getContext().getTtl());
        Acknowledgement.Context.Location location = new Acknowledgement.Context.Location();
        location.setCity(new Acknowledgement.Context.Location.City(initRequest.getContext().getLocation().getCity().getCode()));
        location.setCountry(new Acknowledgement.Context.Location.Country(initRequest.getContext().getLocation().getCountry().getCode()));
        context.setLocation(location);
        context.setVersion(initRequest.getContext().getVersion());
        Acknowledgement.Message message = new Acknowledgement.Message();
        message.setAck(new Acknowledgement.Message.Ack(AckStatus.ACK));
        response.setContext(context);
        response.setMessage(message);
        return response;
    }

    public Acknowledgement kycAck(KycOnStatusRequest kycAck) {

        Acknowledgement response = new Acknowledgement();
        Acknowledgement.Context context = new Acknowledgement.Context();
        context.setAction(kycAck.getContext().getAction());
        context.setBap_id(kycAck.getContext().getBap_id());
        context.setBap_uri(kycAck.getContext().getBap_uri());
        context.setDomain(kycAck.getContext().getDomain());
        context.setMessage_id(kycAck.getContext().getMessage_id());
        context.setTimestamp(java.time.Instant.now().toString());
        context.setTransaction_id(kycAck.getContext().getTransaction_id());
        context.setTtl(kycAck.getContext().getTtl());
        Acknowledgement.Context.Location location = new Acknowledgement.Context.Location();
        location.setCity(new Acknowledgement.Context.Location.City(kycAck.getContext().getLocation().getCity().getCode()));
        location.setCountry(new Acknowledgement.Context.Location.Country(kycAck.getContext().getLocation().getCountry().getCode()));
        context.setLocation(location);
        context.setVersion(kycAck.getContext().getVersion());
        Acknowledgement.Message message = new Acknowledgement.Message();
        message.setAck(new Acknowledgement.Message.Ack(AckStatus.ACK));
        response.setContext(context);
        response.setMessage(message);
        return response;
    }

    public Acknowledgement payAck(PaymentOnStatus payack) {

        Acknowledgement response = new Acknowledgement();
        Acknowledgement.Context context = new Acknowledgement.Context();
        context.setAction(payack.getContext().getAction());
        context.setBap_id(payack.getContext().getBap_id());
        context.setBap_uri(payack.getContext().getBap_uri());
        context.setDomain(payack.getContext().getDomain());
        context.setMessage_id(payack.getContext().getMessage_id());
        context.setTimestamp(java.time.Instant.now().toString());
        context.setTransaction_id(payack.getContext().getTransaction_id());
        context.setTtl(payack.getContext().getTtl());
        context.setVersion(payack.getContext().getVersion());
        Acknowledgement.Context.Location location = new Acknowledgement.Context.Location();
        location.setCity(new Acknowledgement.Context.Location.City(payack.getContext().getLocation().getCity().getCode()));
        location.setCountry(new Acknowledgement.Context.Location.Country(payack.getContext().getLocation().getCountry().getCode()));
        context.setLocation(location);
        Acknowledgement.Message message = new Acknowledgement.Message();
        message.setAck(new Acknowledgement.Message.Ack(AckStatus.ACK));
        response.setContext(context);
        response.setMessage(message);
        return response;
    }

    public  Acknowledgement confirmAckResponse(MotorOnConfirmRequest onConfirmRequest) {

        Acknowledgement response = new Acknowledgement();
        Acknowledgement.Context context = new Acknowledgement.Context();
        context.setAction(onConfirmRequest.getContext().getAction());
        context.setBap_id(onConfirmRequest.getContext().getBap_id());
        context.setBap_uri(onConfirmRequest.getContext().getBap_uri());
        context.setDomain(onConfirmRequest.getContext().getDomain());
        context.setMessage_id(onConfirmRequest.getContext().getMessage_id());
        context.setTimestamp(java.time.Instant.now().toString());
        context.setTransaction_id(onConfirmRequest.getContext().getTransaction_id());
        context.setTtl(onConfirmRequest.getContext().getTtl());
        Acknowledgement.Context.Location location = new Acknowledgement.Context.Location();
        location.setCity(new Acknowledgement.Context.Location.City(onConfirmRequest.getContext().getLocation().getCity().getCode()));
        location.setCountry(new Acknowledgement.Context.Location.Country(onConfirmRequest.getContext().getLocation().getCountry().getCode()));
        context.setLocation(location);
        context.setVersion(onConfirmRequest.getContext().getVersion());
        Acknowledgement.Message message = new Acknowledgement.Message();
        message.setAck(new Acknowledgement.Message.Ack(AckStatus.ACK));
        response.setContext(context);
        response.setMessage(message);
        return response;
    }


    public  Acknowledgement OnStatusAckResponse(MotorOnStatusRequest onStatus) {

        Acknowledgement response = new Acknowledgement();
        Acknowledgement.Context context = new Acknowledgement.Context();
        context.setAction(onStatus.getContext().getAction());
        context.setBap_id(onStatus.getContext().getBap_id());
        context.setBap_uri(onStatus.getContext().getBap_uri());
        context.setDomain(onStatus.getContext().getDomain());
        context.setMessage_id(onStatus.getContext().getMessage_id());
        context.setTimestamp(java.time.Instant.now().toString());
        context.setTransaction_id(onStatus.getContext().getTransaction_id());
        context.setTtl(onStatus.getContext().getTtl());
        Acknowledgement.Context.Location location = new Acknowledgement.Context.Location();
        location.setCity(new Acknowledgement.Context.Location.City(onStatus.getContext().getLocation().getCity().getCode()));
        location.setCountry(new Acknowledgement.Context.Location.Country(onStatus.getContext().getLocation().getCountry().getCode()));
        context.setLocation(location);
        context.setVersion(onStatus.getContext().getVersion());
        Acknowledgement.Message message = new Acknowledgement.Message();
        message.setAck(new Acknowledgement.Message.Ack(AckStatus.ACK));
        response.setContext(context);
        response.setMessage(message);
        return response;
    }



    public  Acknowledgement onUpdateAckResponse(OnUpdateRequest OnUpdate) {

        Acknowledgement response = new Acknowledgement();
        Acknowledgement.Context context = new Acknowledgement.Context();
        context.setAction(OnUpdate.getContext().getAction());
        context.setBap_id(OnUpdate.getContext().getBap_id());
        context.setBap_uri(OnUpdate.getContext().getBap_uri());
        context.setDomain(OnUpdate.getContext().getDomain());
        context.setMessage_id(OnUpdate.getContext().getMessage_id());
        context.setTimestamp(java.time.Instant.now().toString());
        context.setTransaction_id(OnUpdate.getContext().getTransaction_id());
        context.setTtl(OnUpdate.getContext().getTtl());
        Acknowledgement.Context.Location location = new Acknowledgement.Context.Location();
        location.setCity(new Acknowledgement.Context.Location.City(OnUpdate.getContext().getLocation().getCity().getCode()));
        location.setCountry(new Acknowledgement.Context.Location.Country(OnUpdate.getContext().getLocation().getCountry().getCode()));
        context.setLocation(location);
        context.setVersion(OnUpdate.getContext().getVersion());
        Acknowledgement.Message message = new Acknowledgement.Message();
        message.setAck(new Acknowledgement.Message.Ack(AckStatus.ACK));
        response.setContext(context);
        response.setMessage(message);
        return response;
    }




    public  Acknowledgement cancelAckResponse(OnCancelRequest onCancelRequest) {

        Acknowledgement response = new Acknowledgement();
        Acknowledgement.Context context = new Acknowledgement.Context();
        context.setAction(onCancelRequest.getContext().getAction());
        context.setBap_id(onCancelRequest.getContext().getBap_id());
        context.setBap_uri(onCancelRequest.getContext().getBap_uri());
        context.setDomain(onCancelRequest.getContext().getDomain());
        context.setMessage_id(onCancelRequest.getContext().getMessage_id());
        context.setTimestamp(java.time.Instant.now().toString());
        context.setTransaction_id(onCancelRequest.getContext().getTransaction_id());
        context.setTtl(onCancelRequest.getContext().getTtl());
        Acknowledgement.Context.Location location = new Acknowledgement.Context.Location();
        location.setCity(new Acknowledgement.Context.Location.City(onCancelRequest.getContext().getLocation().getCity().getCode()));
        location.setCountry(new Acknowledgement.Context.Location.Country(onCancelRequest.getContext().getLocation().getCountry().getCode()));
        context.setLocation(location);
        context.setVersion(onCancelRequest.getContext().getVersion());
        Acknowledgement.Message message = new Acknowledgement.Message();
        message.setAck(new Acknowledgement.Message.Ack(AckStatus.ACK));
        response.setContext(context);
        response.setMessage(message);
        return response;
    }
    
    public Acknowledgement initAckResponse(MotorOnInitRequest initRequest) {

        Acknowledgement response = new Acknowledgement();
        Acknowledgement.Context context = new Acknowledgement.Context();

        context.setAction(initRequest.getContext().getAction());
        context.setBap_id(initRequest.getContext().getBap_id());
        context.setBap_uri(initRequest.getContext().getBap_uri());
        context.setDomain(initRequest.getContext().getDomain());
        context.setMessage_id(initRequest.getContext().getMessage_id());
        context.setTimestamp(java.time.Instant.now().toString());
        context.setTransaction_id(initRequest.getContext().getTransaction_id());
        context.setTtl(initRequest.getContext().getTtl());
        context.setVersion(initRequest.getContext().getVersion());

        Acknowledgement.Context.Location location =
                new Acknowledgement.Context.Location();

        location.setCity(
                new Acknowledgement.Context.Location.City(
                        initRequest.getContext().getLocation().getCity().getCode()
                )
        );

        location.setCountry(
                new Acknowledgement.Context.Location.Country(
                        initRequest.getContext().getLocation().getCountry().getCode()
                )
        );

        context.setLocation(location);

        Acknowledgement.Message message = new Acknowledgement.Message();
        message.setAck(new Acknowledgement.Message.Ack(AckStatus.ACK));

        response.setContext(context);
        response.setMessage(message);

        return response;
    }

}
