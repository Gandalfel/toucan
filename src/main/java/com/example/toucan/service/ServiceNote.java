package com.example.toucan.service;

import com.example.toucan.model.dto.DtoNote;
import com.example.toucan.model.dto.DtoShortNoteContainer;
import com.example.toucan.model.entity.EntityNote;
import com.example.toucan.repository.RepositoryNote;
import com.example.toucan.repository.RepositoryUser;
import com.example.toucan.service.notedetails.NoteDetailsServiceImpl;
import com.example.toucan.service.userdetails.UserDetailsServiceImpl;
import com.example.toucan.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceNote {

    private final NoteDetailsServiceImpl noteDetailsService;
    private final UserDetailsServiceImpl userDetailsService;
    private final RepositoryNote repositoryNote;
    private final RepositoryUser repositoryUser;
    private ModelMapper modelMapper;
    private final JwtUtil jwtUtil;

    public ServiceNote(NoteDetailsServiceImpl noteDetailsService, UserDetailsServiceImpl userDetailsService,
                       RepositoryNote repositoryNote, RepositoryUser repositoryUser, ModelMapper modelMapper) {
        this.noteDetailsService = noteDetailsService;
        this.userDetailsService = userDetailsService;
        this.repositoryNote = repositoryNote;
        this.repositoryUser = repositoryUser;
        this.modelMapper = modelMapper;
        this.jwtUtil = new JwtUtil();
    }

    public DtoNote getNote(String uuid) {
        if (noteDetailsService.loadNoteByUUID(String.valueOf(uuid)) != null) {
            return modelMapper.map(noteDetailsService.loadNoteByUUID(String.valueOf(uuid)), DtoNote.class);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public void createNote(String token, String title, String content) {
        repositoryNote.save(new EntityNote(
                title,
                content,
                System.currentTimeMillis()/1000,
                repositoryUser.findByUsername(jwtUtil.extractUsername(token.substring(7)))));
    }

    public DtoShortNoteContainer getShortNotesProvider(String token, int fromIndex, int quantity) {
        return getShortNotes(jwtUtil.extractUsername(token.substring(7)), fromIndex, quantity);
    }

    private DtoShortNoteContainer getShortNotes(String username, int fromIndex, int quantity) {
        Pageable pageable = PageRequest.of(fromIndex, quantity);
        System.out.println("eeelllooo: " + repositoryNote.takeForShortNotes(repositoryUser.findByUsername(username).getUuid(), pageable));
        List<EntityNote> entityList = repositoryNote.takeForShortNotes(repositoryUser.findByUsername(username).getUuid(), pageable);
        List<DtoNote> dtoList = new ArrayList<>();

        for(EntityNote n :entityList) {
            dtoList.add(modelMapper.map(n, DtoNote.class));
        }

        return new DtoShortNoteContainer(dtoList);
        //return modelMapper.map(notShortNotesYet, DtoShortNoteContainer.class);
    }


    //todo create filter for ControllerNote WAŻNE BO NIE MA AUTORYZACJI ODSTEPU DO NOTATEK KAZDY MOZE SIE DO NICH DOSTAC
    //todo zrob tak zeby kazdy endpoint zwracal nowy token!!!
}
