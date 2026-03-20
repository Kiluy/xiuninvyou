package com.xiuninvyou.backend.voice;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/voice")
public class VoiceController {

    @PostMapping(value = "/asr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> asr(@RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Map.of("text", "");
        }
        return Map.of("text", "（ASR）我已收到你的语音，已转成文本。请继续聊天。");
    }

    @PostMapping("/tts")
    public Map<String, String> tts(@RequestBody Map<String, String> payload) {
        return Map.of("text", payload.getOrDefault("text", ""));
    }
}
