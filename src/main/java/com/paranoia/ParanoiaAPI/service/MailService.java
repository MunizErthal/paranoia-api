package com.paranoia.ParanoiaAPI.service;

import com.paranoia.ParanoiaAPI.domain.Equipe;
import com.paranoia.ParanoiaAPI.domain.Usuario;
import com.paranoia.ParanoiaAPI.domain.Venda;
import com.paranoia.ParanoiaAPI.domain.VendaItem;
import com.paranoia.ParanoiaAPI.domain.enums.HistoricoAcoes;
import com.paranoia.ParanoiaAPI.exception.ParanoiaException;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import static java.util.Objects.nonNull;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

@Service
public class MailService {
    private static final String PARANOIA_MAIL = "paranoia@paranoiars.com.br";
    private static final String MAIL_PASSWORD = "Par@RF2018noia";
    private static final String PARANOIA_ESCAPE_GAME = "Paranoia Escape Game";
    private static final String BACKGROUND_IMAGE = "background-image: url('cid:background.png');";
    private static final String BACKGROUND_COLOR = "background-color: black; ";
    private static final String END_MESSAGE = "</div><div style='text-align: center; width: 35vw; margin: auto; margin-top: 2vw; margin-bottom: 3vw;'><label style='font-size: 2vw; color: #fff;'> Att. Seus amigos da Paranoia</label></div>";
    private static final String START_MESSAGE = "<div style=\" %s background-attachment: fixed; background-position: center; background-repeat: no-repeat; background-size: cover; border-radius: 4vw; height: auto; width: auto; text-align: center; margin: auto;\">" +
            "<div style='text-align: center; padding: 1vw;'>" +
            "<img  style=\"width: 30vw; height: auto;\" src=\"cid:paranoia.png\">" +
            "</div>" +
            "<div style='text-align: center'>" +
            "<label style='font-size: 4vw; color: #fff; margin-top: 3vw;'> Olá, </label><label style='font-size: 4vw; color: #fff; margin-top: 2vw; text-decoration: underline; text-decoration-color: red;'> %s </label><label style='font-size: 4vw; color: #fff; margin-top: 2vw; text-decoration: underline; text-decoration-color: red;'> %s </label><label style='font-size:4vw; color:#fff; margin-top:2vw;'>!</label>" +
            "</div>" +
            "<div style='text-align: center; width: 45vw; margin: auto; margin-top: 3vw;'>";

    private static DateTimeFormatter DATA_FORMAT = DateTimeFormatter.ofPattern("dd/mm/yyyy HH:mm");

    private final String imageMailPath;

    private final PdfService pdfService;

    @Autowired
    MailService(@Value("${mail.images.path}") String imageMailPath,
                final PdfService pdfService) {
        this.pdfService = pdfService;
        this.imageMailPath = imageMailPath;
    }

    Session session = Session.getDefaultInstance(this.generateMailProperties(), new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(PARANOIA_MAIL, MAIL_PASSWORD);
        }
    });

    public void enviarConfirmacaoDeEmail(final Usuario usuario) {
        try {
            MimeMessage message = this.createMessage("Verificação de E-mail", usuario.getEmail(), PARANOIA_MAIL);
            String messageContent = StringUtils.join(
                    this.buildStartMessage(usuario),
                    StringUtils.join("<label style='font-size: 2vw; color: #fff;'> Obrigado por se cadastrar na Paranoia! Falta pouco para finalizar o seu cadastro agora.</label>",
                            "</div>",
                            "<div style='text-align: center; width: 45vw; margin: auto; margin-top: 3vw;'>",
                            "<label style='font-size: 2vw; color: #fff;'> Por motivos de segurança, nós precisamos verificar seu endereço de e-mail antes de prosseguir, clicando no botão.</label>",
                            "</div>",
                            "<div style='text-align: center; color: #fff; margin-top: 3vw;'>",
                            "<a target='_blank' href='https://paranoiajogos.com.br?hash=" + usuario.getCodigoConfirmacaoEmail() + "'><button onclick=\"window.open('https://paranoiajogos.com.br?hash=" + usuario.getCodigoConfirmacaoEmail() + ", 'minhaJanela', 'height=200,width=200');\" type='button' style='width: 20vw; height: 4vw; font-size: 1.5vw; border-width: 0.2vw !important; width: auto; background-color: red; color: #fff; margin-bottom: 2vw; border: none; border-bottom: solid red; background-color: transparent; cursor: pointer !important;'>Verificar E-mail</button>",
                            "</div>",
                            "<div style='text-align: center; width: 45vw; margin: auto; margin-top: 2vw;'>",
                            "<label style='font-size: 2vw; color: #fff;'> Ou copie e cole este link no seu navegador: https://paranoiajogos.com.br?hash=" + usuario.getCodigoConfirmacaoEmail() + "</label>"),
                    END_MESSAGE);
            this.createMessageContent(messageContent, message);
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParanoiaException(HttpStatus.INTERNAL_SERVER_ERROR, HistoricoAcoes.EMAIL_ERROR, usuario, e.getMessage(), "CONFIRMACAO EMAIL");
        }
    }

    public void enviarEmailResetarSenha(final Usuario usuario) {
        try {
            MimeMessage message = this.createMessage("Recuperação de Senha", usuario.getEmail(), PARANOIA_MAIL);
            String messageContent = StringUtils.join(
                    this.buildStartMessage(usuario),
                    StringUtils.join("<label style='font-size: 2vw; color: #fff;'> Para alterar a sua senha, clique no botão abaixo.</label>",
                            "</div>",
                            "<div style='text-align: center; color: #fff; margin-top: 3vw;'>",
                            "<a target='_blank' href='https://paranoiajogos.com.br/resetar-senha?hash=" + usuario.getCodigoResetarSenha() + "'><button onclick=\"window.open('https://paranoiajogos.com.br/resetar-senha?hash=" + usuario.getCodigoResetarSenha() + ", 'minhaJanela', 'height=200,width=200');\" type='button' style='width: 20vw; height: 4vw; font-size: 1.5vw; border-width: 0.2vw !important; width: auto; background-color: red; color: #fff; margin-bottom: 2vw; border: none; border-bottom: solid red; background-color: transparent; cursor: pointer !important;'>Verificar E-mail</button>",
                            "</div>",
                            "<div style='text-align: center; width: 45vw; margin: auto; margin-top: 2vw;'>",
                            "<label style='font-size: 2vw; color: #fff;'> Ou copie e cole este link no seu navegador: https://paranoiajogos.com.br/resetar-senha?hash=" + usuario.getCodigoResetarSenha() + "</label>"),
                    END_MESSAGE);
            this.createMessageContent(messageContent, message);
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParanoiaException(HttpStatus.INTERNAL_SERVER_ERROR, HistoricoAcoes.EMAIL_ERROR, usuario, e.getMessage(), "RESETAR SENHA");
        }
    }

    public void enviarEmailSuporte(final String assunto,
                                   final String mensagem) {
        try {
            MimeMessage message = this.createMessage(assunto, PARANOIA_MAIL, "suporte@paranoiars.com.br");
            message.setText(mensagem);
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParanoiaException(HttpStatus.INTERNAL_SERVER_ERROR, HistoricoAcoes.EMAIL_ERROR, null, e.getMessage(), "SUPORTE");
        }
    }

    public void enviarEmailVendaEmProcessamento(final Venda venda,
                                                final List<VendaItem> vendaItens,
                                                final String htmlContrato) {
        try {
            MimeMessage message = this.createMessage("Sua reserva está sendo processada", venda.getUsuario().getEmail(), PARANOIA_MAIL);
            String messageContent = StringUtils.join(
                    this.buildStartMessage(venda.getUsuario()),
                            String.format("<label style='font-size: 2vw; color: #fff;'> Sua reserva do jogo %s para o dia %s está sendo processada.</label>", vendaItens.get(0).getProduto().getNome(), venda.getReservadoPara().format(DATA_FORMAT)),
                            "<div style='text-align: center; width: 45vw; margin: auto; margin-top: 3vw;'>",
                            "<label style='font-size: 2vw; color: #fff;'> Para finalizar o pagamente, caso ainda não tenha feito, você pode usar o link abaixo:</label>",
                            "</div>",
                            "<div style='text-align: center; width: 45vw; margin: auto; margin-top: 3vw;'>",
                            "<label style='font-size: 2vw; color: #fff;'> Sua reserva será mantida por 1 hora, após este período, caso o pagamento não seja identificado, ela será removida.</label>",
                            "</div>",
                            "<div style='text-align: center; color: #fff; margin-top: 3vw;'>",
                            "<a target='_blank' href='https://www.mercadopago.com.br/checkout/v1/redirect?pref_id=" + venda.getMercadoPagoId() + "'><button onclick=\"window.open('https://www.mercadopago.com.br/checkout/v1/redirect?pref_id=" + venda.getMercadoPagoId() + ", 'minhaJanela', 'height=200,width=200');\" type='button' style='width: 20vw; height: 4vw; font-size: 1.5vw; border-width: 0.2vw !important; width: auto; background-color: red; color: #fff; margin-bottom: 2vw; border: none; border-bottom: solid red; background-color: transparent; cursor: pointer !important;'>Finalizar pagamento</button>",
                            "</div>",
                            "<div style='text-align: center; width: 45vw; margin: auto; margin-top: 2vw;'>",
                            "<label style='font-size: 2vw; color: #fff;'> Ou copie e cole este link no seu navegador: https://www.mercadopago.com.br/checkout/v1/redirect?pref_id=" + venda.getMercadoPagoId() + "</label>",
                    END_MESSAGE);

            var contrato = pdfService.generatePDFFromHtml(htmlContrato, "contrato", venda.getUsuario());
            this.createMessageContent(messageContent, message, contrato);

            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParanoiaException(HttpStatus.INTERNAL_SERVER_ERROR, HistoricoAcoes.EMAIL_ERROR, venda.getUsuario(), e.getMessage());
        }
    }

    public void enviarEmailVendaFinalizada(final Venda venda,
                                           final List<VendaItem> vendaItens) {
        try {
            MimeMessage message = this.createMessage("Reserva concluída!", venda.getUsuario().getEmail(), PARANOIA_MAIL);
            String messageContent = StringUtils.join(
                            this.buildStartMessage(venda.getUsuario()),
                            String.format("<label style='font-size: 2vw; color: #fff;'> Sua reserva do jogo %s para o dia %s foi concluída com sucesso.</label>", vendaItens.get(0).getProduto().getNome(), venda.getReservadoPara().format(DATA_FORMAT)),
                            END_MESSAGE);
            this.createMessageContent(messageContent, message);
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParanoiaException(HttpStatus.INTERNAL_SERVER_ERROR, HistoricoAcoes.EMAIL_ERROR, venda.getUsuario(), e.getMessage());
        }
    }

    public void enviarEmailVendaPendente(final Venda venda,
                                           final List<VendaItem> vendaItens) {
        try {
            MimeMessage message = this.createMessage("Reserva pendente", venda.getUsuario().getEmail(), PARANOIA_MAIL);
            String messageContent = StringUtils.join(
                            this.buildStartMessage(venda.getUsuario()),
                                    String.format("<label style='font-size: 2vw; color: #fff;'> Sua reserva do jogo %s para o dia %s está pendente.</label>", vendaItens.get(0).getProduto().getNome(), venda.getReservadoPara().format(DATA_FORMAT)),
                                    "<div style='text-align: center; width: 45vw; margin: auto; margin-top: 3vw;'>",
                                    "<label style='font-size: 2vw; color: #fff;'> Para finalizar o pagamente, caso ainda não tenha feito, você pode usar o link abaixo:</label>",
                                    "</div>",
                                    "<div style='text-align: center; width: 45vw; margin: auto; margin-top: 3vw;'>",
                                    "<label style='font-size: 2vw; color: #fff;'> Sua reserva será mantida por 1 hora após a solicitação, após este período, caso o pagamento não seja identificado, ela será removida.</label>",
                                    "</div>",
                                    "<div style='text-align: center; color: #fff; margin-top: 3vw;'>",
                                    "<a target='_blank' href='https://www.mercadopago.com.br/checkout/v1/redirect?pref_id=" + venda.getMercadoPagoId() + "'><button onclick=\"window.open('https://www.mercadopago.com.br/checkout/v1/redirect?pref_id=" + venda.getMercadoPagoId() + ", 'minhaJanela', 'height=200,width=200');\" type='button' style='width: 20vw; height: 4vw; font-size: 1.5vw; border-width: 0.2vw !important; width: auto; background-color: red; color: #fff; margin-bottom: 2vw; border: none; border-bottom: solid red; background-color: transparent; cursor: pointer !important;'>Finalizar pagamento</button>",
                                    "</div>",
                                    "<div style='text-align: center; width: 45vw; margin: auto; margin-top: 2vw;'>",
                                    "<label style='font-size: 2vw; color: #fff;'> Ou copie e cole este link no seu navegador: https://www.mercadopago.com.br/checkout/v1/redirect?pref_id=" + venda.getMercadoPagoId() + "</label>",
                            END_MESSAGE);
            this.createMessageContent(messageContent, message);
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParanoiaException(HttpStatus.INTERNAL_SERVER_ERROR, HistoricoAcoes.EMAIL_ERROR, venda.getUsuario(), e.getMessage());
        }
    }

    public void enviarEmailVendaFalhou(final Venda venda,
                                       final List<VendaItem> vendaItens,
                                       final Boolean expirada) {
        try {
            MimeMessage message = this.createMessage("A reserva falhou", venda.getUsuario().getEmail(), PARANOIA_MAIL);
            String mensagemDeExpiracao = expirada ?
                    "<label style='font-size: 2vw; color: #fff;'> Sua reserva do jogo %s para o dia %s expirou.</label>"
                    : "<label style='font-size: 2vw; color: #fff;'> Sua reserva do jogo %s para o dia %s não pode ser finalizada.</label>";

            String messageContent = StringUtils.join(
                    this.buildStartMessage(venda.getUsuario()),
                    String.format(mensagemDeExpiracao, vendaItens.get(0).getProduto().getNome(), venda.getReservadoPara().format(DATA_FORMAT)),
                    "<div style='text-align: center; width: 45vw; margin: auto; margin-top: 3vw;'>",
                    "<label style='font-size: 2vw; color: #fff;'> Por favor, entre em contato com a Paranoia caso precise de ajuda.</label>",
                    "</div>",
                    END_MESSAGE);
            this.createMessageContent(messageContent, message);
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParanoiaException(HttpStatus.INTERNAL_SERVER_ERROR, HistoricoAcoes.EMAIL_ERROR, venda.getUsuario(), e.getMessage());
        }
    }

    public void enviarConviteParanoiaEEquipe(final Usuario usuario,
                                             final String email,
                                             final Equipe equipe) {
        try {
            String assunto = String.format("%s convidou você para se juntar a Paranoia", usuario.getNome());
            MimeMessage message = this.createMessage(assunto, email, PARANOIA_MAIL);
            String messageContent = StringUtils.join(
                    this.buildStartMessageWithoutUser(email),
                    StringUtils.join(String.format("<label style='font-size: 2vw; color: #fff;'> %s %s convidou você para se juntar a sua equipe: %s.</label>", usuario.getNome(), usuario.getSobrenome(), equipe.getNome()),
                            "</div>",
                            "<div style='text-align: center; color: #fff; margin-top: 3vw;'>",
                            "<a target='_blank' href='https://paranoiajogos.com.br/cadastro?email=" + email + "&equipe=" + equipe.getId() + "'><button onclick=\"window.open('https://paranoiajogos.com.br/cadastro?email=" + email + "&equipe=" + equipe.getId() + ", 'minhaJanela', 'height=200,width=200');\" type='button' style='width: 20vw; height: 4vw; font-size: 1.5vw; border-width: 0.2vw !important; width: auto; background-color: red; color: #fff; margin-bottom: 2vw; border: none; border-bottom: solid red; background-color: transparent; cursor: pointer !important;'>Juntar-se a equipe!</button>",
                            "</div>",
                            "<div style='text-align: center; width: 45vw; margin: auto; margin-top: 2vw;'>",
                            "<label style='font-size: 2vw; color: #fff;'> Ou copie e cole este link no seu navegador: https://paranoiajogos.com.br/cadastro?email=" + email + "&equipe=" + equipe.getId() + "</label>"),
                    END_MESSAGE);
            this.createMessageContent(messageContent, message);
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParanoiaException(HttpStatus.UNPROCESSABLE_ENTITY, HistoricoAcoes.EMAIL_ERROR, usuario, "O email específicado não pertence a nenhum usuário, e não conseguimos enviar um email de convite para ele.");
        }
    }

    private Properties generateMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", "smtp.zoho.com");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.socketFactory.fallback", "false");
        properties.setProperty("mail.smtp.port", "465");
        properties.setProperty("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.store.protocol", "pop3");
        properties.put("mail.transport.protocol", "smtp");
        properties.setProperty("mail.pop3.socketFactory.fallback", "false");
        properties.put("mail.debug", "false");
        properties.put("mail.debug.auth", "false");
        return properties;
    }

    private MimeMessage createMessage(final String subject,
                                      final String sendMail,
                                      final String fromMail) throws UnsupportedEncodingException, MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromMail, PARANOIA_ESCAPE_GAME));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(sendMail));
        message.setSubject(subject);
        return message;
    }

    private void createMessageContent(final String textContent,
                                      final MimeMessage message) throws MessagingException {
        this.createMessageContent(textContent, message, null);
    }

    private void createMessageContent(final String textContent,
                                      final MimeMessage message,
                                      final File anexo) throws MessagingException {
        // Adding the HTML part
        Multipart multipart = new MimeMultipart();
        BodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(textContent,"text/html; charset=iso-8859-1");
        htmlPart.setDisposition(BodyPart.INLINE);
        multipart.addBodyPart(htmlPart);

        // Anexo
        if (nonNull(anexo)) {
            BodyPart anexoMimeBodyPart = new MimeBodyPart();
            DataSource anexoSource = new FileDataSource(anexo);
            anexoMimeBodyPart.setDataHandler(new DataHandler(anexoSource));
            anexoMimeBodyPart.setFileName("contrato.pdf");
            anexoMimeBodyPart.setHeader("Content-ID", "<contrato.pdf>");
            multipart.addBodyPart(anexoMimeBodyPart);
        }

        // Logo
        BodyPart attach = new MimeBodyPart();
        DataSource source = new FileDataSource(imageMailPath + "paranoia.png");
        attach.setDataHandler(new DataHandler(source));
        attach.setFileName("paranoia.png");
        attach.setHeader("Content-ID","<paranoia.png>");
        multipart.addBodyPart(attach);

        // Background
        BodyPart attachBg = new MimeBodyPart();
        DataSource sourceBg = new FileDataSource(imageMailPath + "background.png");
        attachBg.setDataHandler(new DataHandler(sourceBg));
        attachBg.setFileName("background.png");
        attachBg.setHeader("Content-ID","<background.png>");
        multipart.addBodyPart(attachBg);

        message.setContent(multipart);
    }

    private String buildBackground(final String emailSend) {
        return emailSend.contains("gmail") ? BACKGROUND_IMAGE : BACKGROUND_COLOR;
    }

    private String buildStartMessage(final Usuario usuario) {
        return String.format(START_MESSAGE, buildBackground(usuario.getEmail()), usuario.getNome(), usuario.getSobrenome());
    }

    private String buildStartMessageWithoutUser(final String emailSend) {
        return String.format(START_MESSAGE, buildBackground(emailSend), "agente", "");
    }
}
