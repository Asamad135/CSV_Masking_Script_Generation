package masking.function.demo.controller;



import masking.function.demo.model.MaskingRequest;
import masking.function.demo.service.MaskingScriptService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/masking")
public class MaskingController {

    @Autowired
    private MaskingScriptService maskingScriptService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateScript(@RequestBody MaskingRequest request) {
        try {
            String result = maskingScriptService.generateMaskingScript(request);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error generating script: " + e.getMessage());
        }
    }
}
