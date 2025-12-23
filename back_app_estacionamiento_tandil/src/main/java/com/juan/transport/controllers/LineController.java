package com.juan.transport.controllers;

import com.juan.transport.entities.Line;
import com.juan.transport.repositories.LineRepository;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lines")
public class LineController {

    private final LineRepository lineRepository;

    public LineController(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @GetMapping
    public ResponseEntity<List<Line>> getAllLines() {
        return ResponseEntity.ok(lineRepository.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Line>> getActiveLines() {
        return ResponseEntity.ok(lineRepository.findByActiveTrue());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Line> getLineById(@PathVariable Long id) {
        return lineRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Line> createLine(@RequestBody Line line) {

        if (lineRepository.existsByNumber(line.getNumber())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Line savedLine = lineRepository.save(line);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLine);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Line> updateLine(
            @PathVariable Long id,
            @RequestBody Line updatedLine) {

        return lineRepository.findById(id)
                .map(existingLine -> {
                    existingLine.setName(updatedLine.getName());
                    existingLine.setNumber(updatedLine.getNumber());
                    existingLine.setColor(updatedLine.getColor());
                    existingLine.setActive(updatedLine.isActive());

                    return ResponseEntity.ok(lineRepository.save(existingLine));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {

        if (!lineRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        lineRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
