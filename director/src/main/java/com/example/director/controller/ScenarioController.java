package com.example.director.controller;
import com.example.director.model.Scenario;
import com.example.director.model.User;
import com.example.director.repository.ScenarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
// Apache POI Word (.docx)
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import java.io.InputStream;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.LineSpacingRule;
import org.springframework.core.io.ClassPathResource;
import java.io.InputStream;
// Jsoup（HTMLからpタグ抽出）
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



// PDF
import com.lowagie.text.DocumentException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xhtmlrenderer.pdf.ITextRenderer;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.xhtmlrenderer.pdf.ITextRenderer;


import com.example.director.repository.ScenarioRepository;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.IOException;



@Controller
public class ScenarioController {

    @Autowired
    private ScenarioRepository scenarioRepository;

    @GetMapping("/scenario/create")
    public String createScenarioForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ログインが必要です");
        }
        return "scenario/create";
    }

    @PostMapping("/scenario/create")
    public String createScenario(@RequestParam String title, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ログインが必要です");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "タイトルを入力してください");
        }

        Scenario scenario = new Scenario();
        scenario.setUserId(user.getId());
        scenario.setTitle(title);
        scenario.setBody(""); // 初期化
        scenarioRepository.save(scenario);

        return "redirect:/scenario/edit/" + scenario.getId();
    }

    @GetMapping("/scenario/edit/{id}")
    public String editScenario(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ログインが必要です");
        }

        Optional<Scenario> optionalScenario = scenarioRepository.findById(id);
        if (optionalScenario.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "シナリオが見つかりません");
        }

        Scenario scenario = optionalScenario.get();

        // セッションユーザーと一致しない場合は403
        if (!scenario.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "このシナリオにアクセスする権限がありません");
        }

        model.addAttribute("scenario", scenario);
        return "scenario/edit";
    }

    @PostMapping("/scenario/update")
    @ResponseBody
    public String updateScenario(@RequestParam Long id,
                                 @RequestParam String title,
                                 @RequestParam String body,
                                 HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "NG:login_required";
        }

        Optional<Scenario> optionalScenario = scenarioRepository.findById(id);
        if (optionalScenario.isEmpty()) {
            return "NG:not_found";
        }

        Scenario scenario = optionalScenario.get();
        if (!scenario.getUserId().equals(user.getId())) {
            return "NG:no_permission";
        }

        scenario.setTitle(title);
        scenario.setBody(body);
        scenarioRepository.save(scenario);
        return "OK";
    }

    // ScenarioController.java
    @GetMapping("/scenario/export/word/{id}")
    public void exportWord(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Scenario scenario = scenarioRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "シナリオが見つかりません"));

        ClassPathResource template = new ClassPathResource("template.docx");
        try (InputStream is = template.getInputStream(); XWPFDocument document = new XWPFDocument(is)) {
            String bodyHtml = scenario.getBody();
            org.jsoup.nodes.Document htmlDoc = Jsoup.parse(bodyHtml);
            Elements paragraphs = htmlDoc.select("p");

            XWPFParagraph titlePara = document.createParagraph();
            titlePara.setAlignment(ParagraphAlignment.CENTER);
            titlePara.setSpacingAfter(200);
            XWPFRun titleRun = titlePara.createRun();
            titleRun.setBold(true);
            titleRun.setFontSize(20);
            titleRun.setText(scenario.getTitle());

            for (Element p : paragraphs) {
                XWPFParagraph para = document.createParagraph();
                para.setSpacingAfter(0);
                para.setSpacingBefore(0);
                para.setSpacingBetween(1.0, LineSpacingRule.AUTO);
                XWPFRun run = para.createRun();
                run.setFontSize(11);
                run.setFontFamily("Meiryo");
                run.setText(p.text());
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setHeader("Content-Disposition", "attachment; filename=scenario_" + id + ".docx");
            document.write(response.getOutputStream());
        }
    }

    @GetMapping("/scenario/export/pdf/{id}")
    public void exportPdf(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Scenario scenario = scenarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "シナリオが見つかりません"));

        String body = scenario.getBody();
        if (body == null) {
            body = "(本文が未入力です)";
        }
        // XHTML準拠のため、brやimgなどのタグを修正
        String htmlBody = scenario.getBody();
        htmlBody = htmlBody.replaceAll("<br>", "<br/>");
        htmlBody = htmlBody.replaceAll("<hr>", "<hr/>");
        htmlBody = htmlBody.replaceAll("<img([^>]*)>", "<img$1/>");

        // HTML全体の構築
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><head>");
        htmlBuilder.append("<meta charset='UTF-8'/>");
        htmlBuilder.append("<style>");
        htmlBuilder.append("body { font-family: 'Meiryo', sans-serif; }");
        htmlBuilder.append("p { margin-bottom: 0; }");
        htmlBuilder.append("h1 { text-align: center; font-size: 24pt; }");
        htmlBuilder.append("</style>");
        htmlBuilder.append("</head><body>");
        htmlBuilder.append("<h1>").append(scenario.getTitle()).append("</h1>");
        htmlBuilder.append(htmlBody);
        htmlBuilder.append("</body></html>");

        // XHTMLとして整形
        String cleanHtml = Jsoup.parse(htmlBuilder.toString(), "", org.jsoup.parser.Parser.xmlParser()).outerHtml();

        // PDF出力設定
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"scenario_" + id + ".pdf\"");

        // PDFレンダリング
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(cleanHtml);
        renderer.layout();
        try {
            renderer.createPDF(response.getOutputStream());
        } catch (DocumentException e) {
            throw new IOException("PDF出力に失敗しました", e);
        }
    }




}
