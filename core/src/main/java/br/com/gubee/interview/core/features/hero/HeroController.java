package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.assembler.HeroDTOAssembler;
import br.com.gubee.interview.core.assembler.ResumedHeroDTOAssembler;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.dto.ComparedHeroDTO;
import br.com.gubee.interview.model.dto.HeroDTO;
import br.com.gubee.interview.model.dto.ResumedHeroDTO;
import br.com.gubee.interview.model.dto.UpdatedHeroDTO;
import br.com.gubee.interview.model.request.CreateHeroRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/heroes", produces = APPLICATION_JSON_VALUE)
public class HeroController {
    private final HeroService heroService;
    private final HeroDTOAssembler heroDTOAssembler;
    private final ResumedHeroDTOAssembler resumedHeroDTOAssembler;


    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Void> create(@Validated @RequestBody CreateHeroRequest createHeroRequest) {
        final UUID id = heroService.create(createHeroRequest);
        return created(URI.create(format("/api/v1/heroes/%s", id))).build();
    }

    @GetMapping(value = "/{heroId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(@PathVariable UUID heroId) {
        Optional<Hero> heroOptional = heroService.findById(heroId);

        if (heroOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.ok(heroDTOAssembler.toDTO(heroOptional.get()));
    }

    @GetMapping(value = "/search/{heroName}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<HeroDTO> findManyByName(@PathVariable String heroName) {
        List<Hero> heroesList = heroService.findManyByName(heroName);
        return heroDTOAssembler.toCollectionDTO(heroesList);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<ResumedHeroDTO> list()  {
        return resumedHeroDTOAssembler.toCollectionDTO(heroService.findAll());
    }

    @GetMapping(value = "/compare",produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> compare(@RequestParam String hero1Name, @RequestParam String hero2Name)  {
        Optional<Hero> optionalHero1 = heroService.findByName(hero1Name);
        Optional<Hero> optionalHero2 = heroService.findByName(hero2Name);

        if (optionalHero1.isEmpty() || optionalHero2.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        List<ComparedHeroDTO> comparedHeroes = getComparedHeroes(optionalHero1.get(), optionalHero2.get());

        return ResponseEntity.ok(comparedHeroes);
    }

    @PatchMapping(value = "/{heroId}", produces = APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<?> update(@PathVariable UUID heroId, @RequestBody Map<String,Object> fields) {
        Optional<Hero> heroOptional = heroService.findById(heroId);

        if (heroOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        UpdatedHeroDTO updatedHeroDTO = fieldsToDTO(fields);
        heroService.update(heroOptional.get(), updatedHeroDTO);

        return ResponseEntity.status(HttpStatus.OK).body(heroDTOAssembler.toDTO(heroOptional.get()));
    }

    @DeleteMapping("/{heroId}")
    @Transactional
    public ResponseEntity<Void> deleteById(@PathVariable UUID heroId) {
        Optional<Hero> optionalHero = heroService.findById(heroId);

        if (optionalHero.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        heroService.delete(optionalHero.get());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private List<ComparedHeroDTO> getComparedHeroes(Hero hero1, Hero hero2) {
        HeroDTO heroToCompare1 = heroDTOAssembler.toDTO(hero1);
        HeroDTO heroToCompare2 = heroDTOAssembler.toDTO(hero2);

        return Arrays.asList(
                new ComparedHeroDTO(heroToCompare1, heroToCompare2),
                new ComparedHeroDTO(heroToCompare2, heroToCompare1)
        );
    }


    private UpdatedHeroDTO fieldsToDTO(Map<String, Object> fields) {
        ObjectMapper heroMapper = new ObjectMapper();
        heroMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,true);
        //heroMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,true);

        UpdatedHeroDTO heroDTOInput = heroMapper.convertValue(fields, UpdatedHeroDTO.class);
        UpdatedHeroDTO heroDTO = new UpdatedHeroDTO();

        fields.forEach((key, value) -> {
            Field heroField = ReflectionUtils.findField(UpdatedHeroDTO.class, key);
            if (heroField != null)
                heroField.setAccessible(true);

            Object newValue = null;
            if (heroField != null)
                newValue = ReflectionUtils.getField(heroField, heroDTOInput);

            if (heroField != null)
                ReflectionUtils.setField(heroField,heroDTO,newValue);
        });

        return heroDTO;
    }
}
