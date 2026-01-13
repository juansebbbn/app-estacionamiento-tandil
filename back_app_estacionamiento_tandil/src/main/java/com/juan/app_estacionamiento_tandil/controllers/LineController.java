package com.juan.app_estacionamiento_tandil.controllers;

import com.juan.app_estacionamiento_tandil.entities.Line;
import com.juan.app_estacionamiento_tandil.services.LineService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {this.lineService = lineService;}

    @GetMapping("/all")
    public ResponseEntity<List<Line>> getAllLines() {
        return lineService.getAllLines();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Line> getLineById(@PathVariable Long id) {
        return lineService.getLineById(id);
    }

    @PostMapping("/admin/add")
    public ResponseEntity<Line> createLine(@RequestBody Line line) {
        return lineService.createLine(line);
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<Line> updateLine(
            @PathVariable Long id,
            @RequestBody Line updatedLine) {
        return lineService.updateLine(id, updatedLine);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        return lineService.deleteLine(id);
    }
}
