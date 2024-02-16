package com.example.warehousemanagement_team1.service.email;

import com.example.warehousemanagement_team1.model.Orders;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Service
public class EmailServiceImpl implements EmailService{
    @Value("${email}")
    private String emailPort;
    @Autowired
    private JavaMailSender javaMailSender;
    //nhà cung cấp
    @Override
    public void sendEmailToSupplier(Orders order) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
        try {
            mimeMessageHelper.setFrom(emailPort);
            mimeMessageHelper.setTo(order.getSupplier().getEmail());
            mimeMessageHelper.setSubject("Thông báo đơn hàng");

            String emailContent = buildSupplierEmailContent(order);

            mimeMessageHelper.setText(emailContent, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildSupplierEmailContent(Orders order) {
        return
                "Xin chào " + order.getSupplier().getSupplierName() + " , bạn có đơn hàng mới từ:\n" +
                        "- Mã đơn: " + order.getOrderId() + "\n" +
                        "- Người nhận: " + order.getReceiver().getReceiverName() + "\n" +
                        "- Địa chỉ nhận: " + order.getReceiver().getAddress() + "\n" +
                        "\n" +
                        "Do đã quá " + order.getNumberOfFailedDelivery() + " lần thực hiện giao hàng không thành công. Đơn hàng của bạn sẽ được hoàn trả về người gửi hàng.\n" +
                        "\n" +
                        "Trân trọng cảm ơn.";
    }
    //khách hàng
    @Override
    public void sendEmailToRecipient(Orders order) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
        try {
            mimeMessageHelper.setFrom(emailPort);
            mimeMessageHelper.setTo(order.getReceiver().getEmail());
            mimeMessageHelper.setSubject("Thông báo đơn hàng");

            String emailContent = buildRecipientEmailContent(order);

            mimeMessageHelper.setText(emailContent, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildRecipientEmailContent(Orders order) {
        return
                "Xin chào " + order.getReceiver().getReceiverName() + ", bạn có đơn hàng mới từ:\n" +
                        "- Mã đơn: " + order.getOrderId() + "\n" +
                        "- Người gửi: " + order.getSupplier().getSupplierName() + "\n" +
                        "- Địa chỉ gửi: " + order.getSupplier().getAddress() + "\n" +
                        "\n" +
                        "Do đã quá " + order.getNumberOfFailedDelivery() + " lần thực hiện giao hàng không thành công. Đơn hàng của bạn sẽ được hoàn trả về người gửi hàng.\n" +
                        "\n" +
                        "Trân trọng cảm ơn.";
    }

}
