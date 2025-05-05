package masking.function.demo.controller;

import masking.function.demo.model.TableMappingRequest;
import masking.function.demo.service.TableMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/table-mapping")
public class TableMappingController {

    @Autowired
    private TableMappingService tableMappingService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateTableMapping(@RequestBody TableMappingRequest request) {
        try {
            String result = tableMappingService.generateTableMappingScript(request);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error generating table mapping: " + e.getMessage());
        }
    }
}