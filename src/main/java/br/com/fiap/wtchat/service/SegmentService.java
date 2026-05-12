package br.com.fiap.wtchat.service;

import br.com.fiap.wtchat.model.Segment;
import br.com.fiap.wtchat.repository.SegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SegmentService {

    private final SegmentRepository segmentRepository;
    private final AuditService auditService;

    public Segment create(Segment segment, String operatorId) {
        Segment saved = segmentRepository.save(segment);
        auditService.log(operatorId, null, "CREATE_SEGMENT", "Segment", saved.getId(), "Segmento criado: " + saved.getName());
        return saved;
    }

    public List<Segment> findAll() {
        return segmentRepository.findAll();
    }

    public Segment findById(String id) {
        return segmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Segmento não encontrado"));
    }

    public Segment update(String id, Segment updated, String operatorId) {
        Segment segment = findById(id);
        if (updated.getName() != null) segment.setName(updated.getName());
        if (updated.getDescription() != null) segment.setDescription(updated.getDescription());
        if (updated.getCustomerIds() != null) segment.setCustomerIds(updated.getCustomerIds());
        auditService.log(operatorId, null, "UPDATE_SEGMENT", "Segment", id, "Segmento atualizado");
        return segmentRepository.save(segment);
    }

    public void delete(String id, String operatorId) {
        segmentRepository.deleteById(id);
        auditService.log(operatorId, null, "DELETE_SEGMENT", "Segment", id, "Segmento removido");
    }
}
