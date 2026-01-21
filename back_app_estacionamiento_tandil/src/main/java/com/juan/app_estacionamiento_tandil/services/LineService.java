package com.juan.app_estacionamiento_tandil.services;

import com.juan.app_estacionamiento_tandil.entities.Line;
import com.juan.app_estacionamiento_tandil.repositories.LineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private static final Logger logger = LoggerFactory.getLogger(LineService.class);

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public ResponseEntity<List<Line>> getAllLines() {
        logger.info("Getting all lines");
        return ResponseEntity.ok(lineRepository.findAll());
    }

    public ResponseEntity<Line> getLineById(Long id) {
        logger.info("Getting line with id {}", id);
        Optional<Line> line = lineRepository.findById(id);
        if (line.isPresent()) {
            return ResponseEntity.ok(line.get());
        }
        logger.info("No line found with id while fetching{}", id);
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<Line> createLine(Line line) {
        logger.info("Creating line {}", line);
        return ResponseEntity.ok(lineRepository.save(line));
    }

    public ResponseEntity<Line> updateLine(Long id, Line updatedLine) {
        logger.info("Updating line with id {}", id);
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
        logger.info("No line found with id while updating{}", id);
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<Void> deleteLine(Long id) {
        logger.info("Deleting line with id {}", id);
        lineRepository.deleteById(id);
        logger.info("Line with id {} deleted", id);
        return ResponseEntity.noContent().build();
    }
}
