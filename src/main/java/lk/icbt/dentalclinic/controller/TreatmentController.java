package lk.icbt.dentalclinic.controller;
import lk.icbt.dentalclinic.dao.TreatmentDao;
import lk.icbt.dentalclinic.model.Treatment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/treatments")
public class TreatmentController { private final TreatmentDao dao; public TreatmentController(TreatmentDao dao){this.dao=dao;}
 @GetMapping public List<Treatment> all(){return dao.findAll();}
 @PostMapping public ResponseEntity<Treatment> create(@RequestBody Treatment t){t.setId(null);return ResponseEntity.status(201).body(dao.save(t));}
}
