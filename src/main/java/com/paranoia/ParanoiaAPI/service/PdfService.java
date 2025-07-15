package com.paranoia.ParanoiaAPI.service;

import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfWriter;
import com.paranoia.ParanoiaAPI.domain.Usuario;
import com.paranoia.ParanoiaAPI.domain.enums.HistoricoAcoes;
import com.paranoia.ParanoiaAPI.exception.ParanoiaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;

@Service
public class PdfService {
    private final String pdfOutputPath;

    @Autowired
    PdfService(@Value("${pdf.output.path}") String pdfOutputPath) {
        this.pdfOutputPath = pdfOutputPath;
    }

    public File generatePDFFromHtml(final String htmlContrato,
                                    final String nomeArquivo,
                                    final Usuario usuario) {
        try {
            File file = new File(pdfOutputPath + File.separator + nomeArquivo + ".pdf");

            ITextRenderer renderer = new ITextRenderer();
            renderer.getSharedContext().setPrint(true);
            renderer.getSharedContext().setInteractive(false);
            renderer.setDocumentFromString(this.corrigirHTML(htmlContrato));
            renderer.layout();

            try (FileOutputStream fos = new FileOutputStream(file)) {
                renderer.createPDF(fos);
            }

            return file;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ParanoiaException(HttpStatus.INTERNAL_SERVER_ERROR, HistoricoAcoes.PDF_ERROR, usuario, "Não foi possível criar o pdf");
        }
    }

    private String corrigirHTML(String htmlContrato) {
        return "<html lang=\"pt-BR\">" + htmlContrato.replace("<script src=\"https://printjs-4de6.kxcdn.com/print.min.js\"></script>", "")
                .replace("<link href=\"https://printjs-4de6.kxcdn.com/print.min.css\" rel=\"stylesheet\">", "")
                .replace("<meta charset=\"UTF-8\">", "")
                .replace("margin: auto; margin-top: 5vw; margin-bottom: 2vw; text-align: center;", "margin: auto; margin-top: 5vw; margin-bottom: 25px; text-align: center;")
                .replace("margin-left: 4vw;", "margin-left: 60px;")
                .replace("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">", "")
                .replace("<table style=\"font-family: Calibri, sans-serif; margin-top: 30px; margin-bottom: 30px; display: none;\">", "<table style=\"font-family: Calibri, sans-serif; margin-top: 30px;\">")
                .replace("<div style=\"display: flex; margin-top: 6vw; margin-bottom: 5vw;\">", "<div style=\"display: none; margin-top: 6vw; margin-bottom: 5vw;\">")
                .replace("style=\"font-size: 2vw !important; margin: auto;", "style=\"font-size: 15px !important; margin: auto;")
                .replace("<label style=\"font-size: 2.5vw; font-weight: bold; margin: auto;\">", "<label style=\"font-size: 20px; font-weight: bold; margin: auto;\">")
                .replace("<img style=\"width: 60vw; height: auto;\" src=\"https://paranoiajogos.com.br/assets/imgs/logo/logo-preto.png\">", "<img style=\"width: 500px; height: auto;\" src=\"https://paranoiajogos.com.br/assets/imgs/logo/logo-preto.png\"></img>")
                .split("<script>")[0] + "</body></html>";
    }
}