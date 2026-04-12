package com.example.zodeaccount.plugin.util;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.util.Arrays;
import java.util.StringTokenizer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MarkdownToHtmlConverter {

    /**
     * 将 Markdown 字符串转换为 HTML 字符串
     * @param markdown 输入的 Markdown 内容
     * @return 转换后的 HTML 内容
     */
    public static String convertMarkdownToHtml(String markdown) {
        // 配置解析和渲染选项
        MutableDataSet options = new MutableDataSet();

        // 启用扩展 (表格、任务列表等)[2](@ref)[3](@ref)
        options.set(Parser.EXTENSIONS, Arrays.asList(
                com.vladsch.flexmark.ext.tables.TablesExtension.create(),
                com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension.create()
                // 可以添加更多扩展，如：AutolinkExtension、FootnotesExtension 等
        ));

        // 配置表格选项（可选）[2](@ref)
        options.set(com.vladsch.flexmark.ext.tables.TablesExtension.COLUMN_SPANS, false);
        options.set(com.vladsch.flexmark.ext.tables.TablesExtension.APPEND_MISSING_COLUMNS, true);
        options.set(com.vladsch.flexmark.ext.tables.TablesExtension.DISCARD_EXTRA_COLUMNS, true);
        options.set(com.vladsch.flexmark.ext.tables.TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true);

        // 配置其他选项，例如将软换行转换为硬换行[2](@ref)
        // options.set(HtmlRenderer.SOFT_BREAK, "<br />");

        // 构建解析器和渲染器
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // 解析和渲染
        Node document = parser.parse(markdown);
        String html = renderer.render(document);

        // 可以在这里包装完整的 HTML 结构并添加 CSS 样式（见下一步）
        return wrapWithHtmlTemplate(html);
    }

    /**
     * 内联样式CSS
     * @param html
     * @return
     */
    public static String inlineCss(String html) {
        Document doc = Jsoup.parse(html);
        Elements styleElements = doc.select("style"); // 获取所有<style>标签

        for (Element styleElement : styleElements) {
            String styleRules = styleElement.html().replace("\n", "").trim(); // 获取样式规则并清理
            StringTokenizer tokenizer = new StringTokenizer(styleRules, "{}", true); // 使用{}作为分隔符

            StringBuilder currentSelector = new StringBuilder();
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token.equals("{")) {
                    // 此时currentSelector中存储的是选择器
                    String properties = tokenizer.nextToken(); // 获取属性部分
                    if (!tokenizer.hasMoreTokens()) break;
                    tokenizer.nextToken(); // 跳过接下来的"}"

                    if (!currentSelector.toString().contains(":")) { // 简单跳过伪类
                        Elements selectedElements = doc.select(currentSelector.toString());
                        for (Element elem : selectedElements) {
                            String existingStyle = elem.attr("style");
                            String newStyle = existingStyle + (existingStyle.isEmpty() ? "" : "; ") + properties;
                            elem.attr("style", newStyle.trim());
                        }
                    }
                    currentSelector.setLength(0); // 清空选择器以备下次使用
                } else if (!token.equals("}")) {
                    currentSelector.append(token);
                }
            }
            styleElement.remove(); // 可选：移除已内联的<style>标签
        }
        return doc.body().toString();
    }

    /**
     * 将渲染后的 HTML 内容包装到完整的 HTML 文档中，并添加 CSS 样式
     * @param content 渲染后的 HTML 内容
     * @return 完整的 HTML 文档
     */
    private static String wrapWithHtmlTemplate(String content) {
        // 这里是一个简单的 GitHub Flavored Markdown 风格的 CSS，你可以替换或扩展它[2](@ref)
        String style = "<style>\n" +
                "body {width: 375px;padding: 20px;position: relative;font-family: Optima-Regular, Optima, PingFangSC-light, PingFangTC-light, 'PingFang SC', Cambria, Cochin, Georgia, Times, 'Times New Roman', serif;font-size: 14px;line-height: 1.75;text-align: left;pointer-events: initial !important;}\n" +
                "h1{font-family: Optima-Regular, Optima, PingFangSC-light, PingFangTC-light, 'PingFang SC', Cambria, Cochin, Georgia, Times, 'Times New Roman', serif;padding: 0.5em 1em;border-bottom: 2px solid #FA5151;font-size: 19.6px;text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);margin: 0.67em auto;font-weight: bold;text-align: center;color: hsl(0 0% 6% / 1);}\n" +
                "h2{font-family: Optima-Regular, Optima, PingFangSC-light, PingFangTC-light, 'PingFang SC', Cambria, Cochin, Georgia, Times, 'Times New Roman', serif;padding: 0.3em 1em;border-radius: 8px;font-size: 18.2px;box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);margin: 2.5em auto 2em;color: #fff;background: #FA5151;font-weight: bold;text-align: center;}\n" +
                "h3{font-family: Optima-Regular, Optima, PingFangSC-light, PingFangTC-light, 'PingFang SC', Cambria, Cochin, Georgia, Times, 'Times New Roman', serif;padding-left: 12px;font-size: 16.8px;border-left: 4px solid #FA5151;border-bottom: 1px dashed #FA5151;color: hsl(0 0% 6% / 1);font-weight: bold;line-height: 1.2;}\n" +
                "h4{font-family: Optima-Regular, Optima, PingFangSC-light, PingFangTC-light, 'PingFang SC', Cambria, Cochin, Georgia, Times, 'Times New Roman', serif;margin: 2em 8px 0.5em;color: #FA5151;font-size: 15.4px;font-weight: bold;}\n" +
                "h5{font-family: Optima-Regular, Optima, PingFangSC-light, PingFangTC-light, 'PingFang SC', Cambria, Cochin, Georgia, Times, 'Times New Roman', serif;margin: 1.5em 8px 0.5em;color: #FA5151;font-size: 14px;font-weight: bold;}\n" +
                "h6{font-family: Optima-Regular, Optima, PingFangSC-light, PingFangTC-light, 'PingFang SC', Cambria, Cochin, Georgia, Times, 'Times New Roman', serif;margin: 1.5em 8px 0.5em;font-size: 14px;color: #FA5151;}\n" +
                "a {font-family: Optima-Regular, Optima, PingFangSC-light, PingFangTC-light, 'PingFang SC', Cambria, Cochin, Georgia, Times, 'Times New Roman', serif;color: #0366d6; text-decoration: none;}\n" +
                "a:hover {text-decoration: underline;}\n" +
                "code {background-color: #f6f8fa; padding: 0.2em 0.4em; border-radius: 3px; font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;}\n" +
                "pre {background-color: #f6f8fa; padding: 1em; overflow: auto; border-radius: 3px;}\n" +
                "pre code {padding: 0;}\n" +
                "strong {color: #FA5151;font-weight: bold;font-size: inherit;}\n" +
                "blockquote {font-size: 14px;line-height: 1.75;text-align: left;background: #f7f7f7;border-color: hsl(0 0% 89.8%);font-style: italic;padding: 1em 1em 1em 2em;border-left: 4px solid #FA5151;border-radius: 6px;color: rgba(0, 0, 0, 0.6);box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);margin:  0 0 1em 0;}\n" +
                "table {border-collapse: collapse; width: 100%;}\n" +
                "th, td {border: 1px solid #dfe2e5; padding: 0.5em 1em;}\n" +
                "th {background-color: #f6f8fa; font-weight: 600;}\n" +
                "hr {height: 1px;border: none;margin: 2em 0;background: linear-gradient(to right, rgba(0, 0, 0, 0), rgba(0, 0, 0, 0.1), rgba(0, 0, 0, 0));}\n" +
                "ul, ol {list-style-type: disc;padding-left: 1.5em;margin: 10px 0;}\n" +
                "li {font-family: Optima-Regular, Optima, PingFangSC-light, PingFangTC-light, 'PingFang SC', Cambria, Cochin, Georgia, Times, 'Times New Roman', serif;font-size: 14px;line-height: 1.75;text-align: left;margin: 0.5em 8px;color: hsl(0 0% 6% / 1);}\n" +
                "p {font-family: Optima-Regular, Optima, PingFangSC-light, PingFangTC-light, 'PingFang SC', Cambria, Cochin, Georgia, Times, 'Times New Roman', serif;font-size: 14px;line-height: 1.75;text-align: left;letter-spacing: 0.1em;color: hsl(0 0% 3.9%);border-color: hsl(0 0% 89.8%);}\n" +
                "</style>";

        return "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Markdown 转换结果</title>\n" +
                style +
                "</head>\n" +
                "<body class=\"markdown-body\">\n" + // 添加一个类名以便更精确地应用样式
                content +
                "</body>\n" +
                "</html>";
    }

    public static String parseMarkdown(String content) {
        // 1. (可选但重要) 如果内容来自不可信源，先进行 HTML 转义以防止 XSS[9](@ref)[10](@ref)
        // 使用 Spring 的 HtmlUtils (推荐) 或 Apache Commons Text 的 StringEscapeUtils
        // String safeMarkdown = HtmlUtils.htmlEscape(compressedMarkdown); // 转义 HTML 特殊字符
        // 注意：转义后，Markdown 中的特殊符号也会被转义，所以通常是在最终渲染 HTML 输出前对动态部分进行转义，而不是在 Markdown 转换前。
        // 更佳实践：在将最终生成的 HTML 输出到前端页面时，由模板引擎（如 Thymeleaf, FreeMarker）自动转义，或使用 HtmlUtils.htmlEscape() 处理其中嵌入的不信任变量。

        System.out.println(content);
        content = content.replace("\\n", "\n");
        // 2. 转换 Markdown 到 HTML
        String styledHtml = MarkdownToHtmlConverter.convertMarkdownToHtml(content);
        String inlinedHtml = inlineCss(styledHtml);
        // 3. 输出或使用生成的 HTML
        System.out.println(inlinedHtml); // 输出到控制台
        // 或者写入文件、发送到 HTTP 响应等
        return inlinedHtml;
    }

    public static void main(String[] args) {
        // 你的压缩 Markdown 内容 (可能来自文件、数据库或用户输入)
        String compressedMarkdown = "\n# 地缘风险解除，A股放量反攻！接下来如何布局？\n\n> 作者：老徐  \n> 来源：公众号「老徐看盘」  \n> 发布时间：4月9日  \n\n---\n\n## \uD83D\uDCC8 今日盘面：放量大涨，情绪全面修复\n\n- **成交量**：两市合计成交 **8200亿+**，较前几日地量显著放大；\n- **上涨家数**：超 **5100家** 飘红，普涨格局；\n- **走势特征**：早盘高开后单边上行，尾盘涨幅扩大，沪指收出 **百点阳线**，逼近4000点；\n- **资金动向**：各路资金跑步进场，放量金叉形成，大资金入场信号明确。\n\n---\n\n## \uD83C\uDF0D 核心驱动：中东停火，风险偏好回升\n\n### 停火原因解析：\n1. **美国方面**：担忧开战引爆油价→推升通胀→冲击经济与大选连任，被迫“找台阶下”；\n2. **伊朗方面**：战略目标基本达成，借停火巩固战果，争取谈判筹码（如霍尔木兹海峡控制权、赔偿等）；\n3. **市场影响**：地缘风险解除，压制市场的最大外部不确定性落地，情绪迅速修复。\n\n> ✅ 信号意义：**靴子落地，反弹启动**。无论此前方向如何选择，此刻放量突破即是趋势信号。\n\n---\n\n## \uD83D\uDD0D 下一阶段关键：财报季定主线\n\n- **时间窗口**：4月为财报密集披露期，**一季度业绩**将决定5-6月真空期的炒作主线；\n- **机构动向**：当前机构仓位偏低，观望情绪浓，业绩超预期方向有望吸引增量资金；\n- **操作策略**：紧盯财报，按“三三法则”分批布局，**看长做短**，不必焦虑踏空。\n\n---\n\n## \uD83D\uDE80 重点板块方向（按逻辑排序）\n\n### 1. **科技成长线**（情绪+基本面双击）\n- **算力硬件**：AI视频/图像生成商用加速，台积电光整合平台量产在即；\n- **PCB材料**：一季度涨价预期明确；\n- **AI电力/储能电池设备**：装机放量，一季报高增长可期；\n- **半导体/CPO/IT服务**：超跌+补涨+情绪修复，全线爆发。\n\n### 2. **出海与高端制造**\n- **海运**：群体状态金叉，流动性翻红，趋势修复中；\n- **出海链**：受益于全球供应链重构，业绩确定性高。\n\n### 3. **其他热点轮动**\n- 贵金属、互联网保险、快手概念、质谱仪、其他电子、通信设备、小红书概念、AI PC、元器件等今日领涨，多为情绪修复下的补涨或事件驱动。\n\n---\n\n## \uD83D\uDCC5 明日（周四）应对策略\n\n### 市场预判：\n- **大概率高开**（受外盘反弹带动），但**涨幅小于今日**；\n- **盘中震荡加剧**，筹码交换充分，成交量可能不低于今日；\n- **散户跟风情绪有限**，不易形成一致做多合力，属“第一个小分歧日”。\n\n### 操作建议：\n- **持仓者**：安心持股，尤其已布局财报预期方向；\n- **短线交易者**：可低吸，**严禁追高**，围绕圈子里提示的方向操作；\n- **空仓者**：等待盘中分歧后的低吸机会，重点关注**业绩预增+趋势刚启动**标的。\n\n---\n\n## \uD83D\uDCA1 老徐总结\n\n1. **黑天鹅修复第一阶段完成**，市场回归自身节奏（1月14日主动调整后的上升通道）；\n2. **放量金叉+流动资金翻红**，技术面确认反转信号；\n3. **接下来2-3周是财报窗口期**，业绩为王，主线将逐步清晰；\n4. **外围若不再出幺蛾子**，站稳趋势通道上方，则反弹演变为趋势反转。\n\n> \uD83D\uDCCC 记住：信号已给出，按计划执行，少一点主观臆测，多一点纪律操作。\n\n---\n\n**觉得有帮助？请点赞、投币、关注支持一下！**  \n老徐需要你们的数据支持，才能持续输出干货。  \n今晚群内细聊 **周四、周五布局方向**，带你一起吃肉！\n\n**我们明天见！**\n";

        // 1. (可选但重要) 如果内容来自不可信源，先进行 HTML 转义以防止 XSS[9](@ref)[10](@ref)
        // 使用 Spring 的 HtmlUtils (推荐) 或 Apache Commons Text 的 StringEscapeUtils
        // String safeMarkdown = HtmlUtils.htmlEscape(compressedMarkdown); // 转义 HTML 特殊字符
        // 注意：转义后，Markdown 中的特殊符号也会被转义，所以通常是在最终渲染 HTML 输出前对动态部分进行转义，而不是在 Markdown 转换前。
        // 更佳实践：在将最终生成的 HTML 输出到前端页面时，由模板引擎（如 Thymeleaf, FreeMarker）自动转义，或使用 HtmlUtils.htmlEscape() 处理其中嵌入的不信任变量。

        // 2. 转换 Markdown 到 HTML
        String styledHtml = MarkdownToHtmlConverter.convertMarkdownToHtml(compressedMarkdown);
        String inlinedHtml = inlineCss(styledHtml);
        // 3. 输出或使用生成的 HTML
        System.out.println(inlinedHtml); // 输出到控制台
        // 或者写入文件、发送到 HTTP 响应等
    }

}
