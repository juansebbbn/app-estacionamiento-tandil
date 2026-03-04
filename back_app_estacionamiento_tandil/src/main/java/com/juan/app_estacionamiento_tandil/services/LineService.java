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
        logger.info("[LINE] [getAllLines] START");
        
        List<Line> lines = lineRepository.findAll();
        
        logger.info("[LINE] [getAllLines] SUCCESS - line_count={}", lines.size());
        return ResponseEntity.ok(lines);
    }

    public ResponseEntity<Line> getLineById(Long id) {
        logger.info("[LINE] [getLineById] START - id={}", id);
        
        Optional<Line> line = lineRepository.findById(id);
        if (line.isPresent()) {
            logger.info("[LINE] [getLineById] SUCCESS - id={}", id);
            return ResponseEntity.ok(line.get());
        }
        
        logger.warn("[LINE] [getLineById] NOT_FOUND - id={}", id);
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<Line> createLine(Line line) {
        logger.info("[LINE] [createLine] START - name={}, number={}", line.getName(), line.getNumber());
        
        Line savedLine = lineRepository.save(line);
        
        logger.info("[LINE] [createLine] SUCCESS - id={}, name={}", savedLine.getId(), savedLine.getName());
        return ResponseEntity.ok(savedLine);
    }

    public ResponseEntity<Line> updateLine(Long id, Line updatedLine) {
        logger.info("[LINE] [updateLine] START - id={}, new_name={}, new_number={}", id, updatedLine.getName(), updatedLine.getNumber());
        
        Optional<Line> line = lineRepository.findById(id);
        if (line.isPresent()) {
            Line db_line = line.get();
            db_line.setName(updatedLine.getName());
            db_line.setColor(updatedLine.getColor());
            db_line.setNumber(updatedLine.getNumber());
            
            Line savedLine = lineRepository.save(db_line);
            logger.info("[LINE] [updateLine] SUCCESS - id={}, name={}", id, savedLine.getName());
            return ResponseEntity.ok(savedLine);
        }
        
        logger.warn("[LINE] [updateLine] NOT_FOUND - id={}", id);
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<Void> deleteLine(Long id) {
        logger.info("[LINE] [deleteLine] START - id={}", id);
        
        lineRepository.deleteById(id);
        
        logger.info("[LINE] [deleteLine] SUCCESS - id={}", id);
        return ResponseEntity.noContent().build();
    }
}
