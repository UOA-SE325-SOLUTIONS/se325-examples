package uoa.se325.parolees.controller;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import uoa.se325.parolees.dto.ParoleeDTO;
import uoa.se325.parolees.model.Conviction;
import uoa.se325.parolees.model.Movement;
import uoa.se325.parolees.model.Parolee;
import uoa.se325.parolees.repository.ParoleeRepository;
import uoa.se325.parolees.service.MovementBroadcastService;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/parolees")
public class ParoleeController {

    private final ParoleeRepository repo;
    private final MovementBroadcastService movementBroadcastService;

    @Autowired
    public ParoleeController(ParoleeRepository repo, MovementBroadcastService movementBroadcastService) {
        this.repo = repo;
        this.movementBroadcastService = movementBroadcastService;
    }

    /**
     * Adds a new Parolee to the system. The state of the new Parolee is
     * described by a Parolee object.
     *
     * @param dto the Parolee data included in the HTTP request body.
     */
    @PostMapping
    public ResponseEntity<ParoleeDTO> createParolee(@RequestBody ParoleeDTO dto) {
        Parolee parolee = dto.toDomain();
        Parolee saved = repo.save(parolee);

        return ResponseEntity
                .created(URI.create("/parolees/" + saved.getId()))
                .body(ParoleeDTO.fromDomain(saved));
    }

    /**
     * Records a new Movement for a particular Parolee.
     *
     * @param id       the unique identifier of the Parolee.
     * @param movement the timestamped latitude/longitude position of the Parolee.
     */
    @PostMapping("{id}/movements")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void createMovementForParolee(@PathVariable("id") long id,
                                         @RequestBody Movement movement) {

        Parolee parolee = repo
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        parolee.addMovement(movement);
        repo.save(parolee);

        // Notify all WebSocket subscribers of the movement
        ParoleeDTO dto = ParoleeDTO.fromDomain(parolee);
        movementBroadcastService.broadcastMovementUpdate(dto);
    }

    /**
     * Updates an existing Parolee. The parts of a Parolee that can be updated
     * are those represented by a Parolee instance.
     *
     * @param dto the Parolee data included in the HTTP request body.
     */
    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void updateParolee(@PathVariable("id") long id,
                              @RequestBody ParoleeDTO dto) {

        if (dto.getId() != null && !dto.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in the DTO must match the path variable ID.");
        }

        Parolee parolee = repo
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        dto.updateDomain(parolee);
        repo.save(parolee);
    }

    /**
     * Updates the set of disassociate Parolees for a given Parolee.
     *
     * @param id              the Parolee whose disassociates should be updated.
     * @param disassociateIds the new set of disassociates.
     */
    @PutMapping("{id}/disassociates")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void updateDisassociates(@PathVariable("id") long id,
                                    @RequestBody List<Long> disassociateIds) {

        Parolee parolee = repo
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Parolee> disassociates = repo.findAllById(disassociateIds);

        parolee.setDisassociates(new HashSet<>(disassociates));

        repo.save(parolee);
    }

    /**
     * Updates a Parolee's set of convictions.
     *
     * @param id          the unique identifier of the Parolee.
     * @param convictions the Parolee's updated criminal profile.
     */
    @PutMapping("{id}/convictions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void updateConvictions(@PathVariable("id") long id,
                                  @RequestBody List<Conviction> convictions) {

        Parolee parolee = repo
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        parolee.setConvictions(new HashSet<>(convictions));
        repo.save(parolee);
    }

    /**
     * Returns a particular Parolee. The returned Parolee is represented by a
     * Parolee object.
     *
     * @param id the unique identifier of the Parolee.
     */
    @GetMapping("{id}")
    public ParoleeDTO getParolee(@PathVariable("id") long id) {
        Parolee domain = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ParoleeDTO.fromDomain(domain);
    }

    /**
     * Returns a view of the Parolee database, represented as a List of
     * Parolee objects.
     *
     * @param page Optional query parameter. If provided, enables pagination.
     *             If provided, must be >= 0.
     * @param size If pagination enabled, this query parameter will determine the
     *             page size. Must be >= 1.
     */
    @GetMapping
    public ResponseEntity<List<ParoleeDTO>> getAllParolees(@RequestParam(value = "page", required = false) Integer page,
                                                           @RequestParam(value = "size", defaultValue = "10") int size) {

        if (page == null) return ResponseEntity.ok(
                repo.findAll().stream().map(ParoleeDTO::fromDomain).toList());

        if (page < 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (size <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        var pageable = PageRequest.of(page, size);
        Page<Parolee> paroleesPage = repo.findAll(pageable);

        var builder = ResponseEntity.ok();

        if (page > 0) {
            builder.header("Prev-Page", String.valueOf(page - 1));
        }

        if (page < paroleesPage.getTotalPages() - 1) {
            builder.header("Next-Page", String.valueOf(page + 1));
        }

        return builder.body(paroleesPage.stream().map(ParoleeDTO::fromDomain).toList());
    }

    /**
     * Returns movement history for a particular Parolee.
     *
     * @param id the unique identifier of the Parolee.
     */
    @GetMapping("{id}/movements")
    @Transactional
    public List<Movement> getMovementsForParolee(@PathVariable("id") long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getMovements();
    }

    /**
     * Returns the dissassociates associated directly with a particular Parolee.
     * Each dissassociate is represented as an instance of class
     * Parolee.
     *
     * @param id the unique identifier of the Parolee.
     */
    @GetMapping("{id}/disassociates")
    @Transactional
    public List<ParoleeDTO> getDisassociatesForParolee(@PathVariable("id") long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getDisassociates()
                .stream()
                .map(ParoleeDTO::fromDomain)
                .toList();
    }

    /**
     * Returns the convictions for a particular Parolee.
     *
     * @param id the unique identifier of the Parolee.
     */
    @GetMapping("{id}/convictions")
    @Transactional
    public Set<Conviction> getConvictionsForParolee(@PathVariable("id") long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getConvictions();
    }
}
