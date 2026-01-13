package com.juan.app_estacionamiento_tandil.services;

import com.juan.app_estacionamiento_tandil.entities.Line;
import com.juan.app_estacionamiento_tandil.repositories.LineRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public ResponseEntity<List<Line>> getAllLines() {
        System.out.println("Fetching all lines");
        return ResponseEntity.ok(lineRepository.findAll());
    }

    public ResponseEntity<Line> getLineById(Long id) {
        System.out.println("Fetching line by id: " + id);
        Optional<Line> line = lineRepository.findById(id);
        if (line.isPresent()) {
            return ResponseEntity.ok(line.get());
        }
        System.out.println("Could not find line by id: " + id);
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<Line> createLine(Line line) {
        System.out.println("Creating line: " + line);
        return ResponseEntity.ok(lineRepository.save(line));
    }

    public ResponseEntity<Line> updateLine(Long id, Line updatedLine) {
        System.out.println("Updating line: " + updatedLine);
        Optional<Line> line = lineRepository.findById(id);
        if (line.isPresent()) {
            Line db_line = line.get();
            db_line.setName(updatedLine.getName());
            db_line.setColor(updatedLine.getColor());
            db_line.setNumber(updatedLine.getNumber());
            db_line.setName(updatedLine.getName());
            lineRepository.save(db_line);
            return ResponseEntity.ok(lineRepository.save(line.get()));
        }
        System.out.println("Could not find line by id: " + id);
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<Void> deleteLine(Long id) {
        System.out.println("Deleting line: " + id);
        lineRepository.deleteById(id);
        System.out.println("Deleted line: " + id);
        return ResponseEntity.noContent().build();
    }
}
