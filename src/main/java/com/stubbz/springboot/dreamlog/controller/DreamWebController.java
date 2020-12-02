package com.stubbz.springboot.dreamlog.controller;

import com.stubbz.springboot.dreamlog.domain.DreamEntry;
import com.stubbz.springboot.dreamlog.domain.CommentEntry;
import com.stubbz.springboot.dreamlog.service.CommentService;
import com.stubbz.springboot.dreamlog.service.DreamService;
import com.stubbz.springboot.dreamlog.service.UserService;
import com.stubbz.springboot.dreamlog.service.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DreamWebController {

    @Autowired
    private DreamService dreamService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserServiceImp userService;

    private static final String DREAMS_TEMPLATE_ID = "dreams";
    private static final String COMMENTS_TEMPLATE_ID = "comments";

    private static final String PUBLIC_DREAMS_TEMPLATE_ID = "pDreams";
    //html file
    private static final String DREAMLOG_TEMPLATE = "dreamlog";
    private static final String EDIT_DREAM_TEMPLATE = "editDream";
    private static final String SHOW_DREAM_TEMPLATE = "showDream";
    private static final String SHOW_PUBLIC_DREAMS_TEMPLATE = "publicDreams";


    private static final String HOMEPAGE_REDIRECT = "redirect:/";
    private static final String SHOW_DREAM_REDIRECT = "redirect:/{id}";

    private static final String DREAMS_TEMPLATE_NEW_DREAM = "newDream";
    private static final String DREAMS_TEMPLATE_NEW_COMMENT = "newComment";
    private static final String DREAMLOG_FORM_HEADER_ID = "formHeader";
    private static final String USERNAME = "accountUsername";
    private static final String COMMENTS_AMOUNT = "commentsAmount";

    @GetMapping("/")
    public String displayDreamLog(Principal principal, Model model, @ModelAttribute(DREAMS_TEMPLATE_NEW_DREAM) DreamEntry newEntry) {

        if (newEntry.getType() == null) {
            model.addAttribute(DREAMLOG_FORM_HEADER_ID, "Add a New Dream");
            model.addAttribute(DREAMS_TEMPLATE_ID, this.dreamService.getOwnDreams(userService.getUsernameByEmail(principal.getName())));
            model.addAttribute(DREAMS_TEMPLATE_NEW_DREAM, new DreamEntry());
            model.addAttribute(USERNAME, userService.getUsernameByEmail(principal.getName()));
        } else {

            if (newEntry.getType().equals("")) {
                model.addAttribute(DREAMLOG_FORM_HEADER_ID, "Add a New Dream");
                model.addAttribute(DREAMS_TEMPLATE_ID, this.dreamService.getOwnDreams(userService.getUsernameByEmail(principal.getName())));
                model.addAttribute(DREAMS_TEMPLATE_NEW_DREAM, new DreamEntry());
                model.addAttribute(USERNAME, userService.getUsernameByEmail(principal.getName()));
            } else {
                List<DreamEntry> filteredList = new ArrayList<DreamEntry>();

                if(newEntry.getAuthor().equals("title"))
                {
                    model.addAttribute(DREAMS_TEMPLATE_ID, this.dreamService.getOwnDreamsByTitle(userService.getUsernameByEmail(principal.getName()), newEntry.getType()));

                }
                else if(newEntry.getAuthor().equals("type"))
                {
                    model.addAttribute(DREAMS_TEMPLATE_ID, this.dreamService.getOwnDreamsByType(userService.getUsernameByEmail(principal.getName()), newEntry.getType()));

                }

                }
                model.addAttribute(DREAMLOG_FORM_HEADER_ID, "Add a New Dream");
                model.addAttribute(DREAMS_TEMPLATE_NEW_DREAM, new DreamEntry());
                model.addAttribute(USERNAME, userService.getUsernameByEmail(principal.getName()));
            }



        return DREAMLOG_TEMPLATE;
    }

    @PostMapping("/")
    public String addDream(Principal principal, Model model, @Valid @ModelAttribute(DREAMS_TEMPLATE_NEW_DREAM) DreamEntry newEntry, BindingResult bindingResult) {
        newEntry.setAuthor(userService.getUsernameByEmail(principal.getName()));
       newEntry = dreamService.trimInput(newEntry);
        if (!bindingResult.hasErrors()) {
            this.dreamService.saveDream(newEntry);
            return HOMEPAGE_REDIRECT;
        } else {
            model.addAttribute(DREAMLOG_FORM_HEADER_ID, "Fill in all fields!");
            model.addAttribute(DREAMS_TEMPLATE_ID, this.dreamService.getOwnDreams(userService.getUsernameByEmail(principal.getName())));
            model.addAttribute(USERNAME, userService.getUsernameByEmail(principal.getName()));
            return DREAMLOG_TEMPLATE;
        }


    }


    @GetMapping("/delete/{id}")
    public String deleteDream(Principal principal, @PathVariable long id) {
        if (dreamService.getDream(id).getAuthor().equals(userService.getUsernameByEmail(principal.getName()))) {
            this.dreamService.deleteDream(id);
            this.commentService.deleteCommentByDream(id);
            return HOMEPAGE_REDIRECT;
        }
        return HOMEPAGE_REDIRECT;
    }

    @GetMapping("/{dreamId}")
    public String showDream(Principal principal, Model model,  @PathVariable long dreamId) {
        if (this.dreamService.getDream(dreamId).getAuthor().equals(userService.getUsernameByEmail(principal.getName())) || this.dreamService.getDream(dreamId).getVisibility() != null) {


            model.addAttribute(DREAMS_TEMPLATE_NEW_DREAM, this.dreamService.getDreamConverted(dreamId));
            model.addAttribute(DREAMS_TEMPLATE_NEW_COMMENT, new CommentEntry());
            model.addAttribute(USERNAME, userService.getUsernameByEmail(principal.getName()));
            model.addAttribute(COMMENTS_TEMPLATE_ID, this.commentService.getDreamCommentsConverted(dreamId));
            model.addAttribute(COMMENTS_AMOUNT, this.commentService.getDreamCommentsConverted(dreamId).size());

            return SHOW_DREAM_TEMPLATE;
        }
        return HOMEPAGE_REDIRECT;
    }

    @PostMapping("/{dreamId}")
    public String postComment(Principal principal, Model model,
                              @PathVariable long dreamId,
                              @Valid @ModelAttribute(DREAMS_TEMPLATE_NEW_COMMENT) CommentEntry newComment,
                              BindingResult bindingResult) {
        if (this.dreamService.getDream(dreamId).getVisibility() != null) {

            newComment = commentService.trimInput(newComment);
            if (!bindingResult.hasErrors()) {
                newComment.setAuthor(userService.getUsernameByEmail(principal.getName()));
                newComment.setConnectedDreamId(dreamId);
                this.commentService.saveComment(newComment);
                return "redirect:/" + Long.toString(dreamId);
            } else {
                model.addAttribute(DREAMS_TEMPLATE_NEW_DREAM, this.dreamService.getDreamConverted(dreamId));
                model.addAttribute(USERNAME, userService.getUsernameByEmail(principal.getName()));
                model.addAttribute(COMMENTS_TEMPLATE_ID, this.commentService.getDreamCommentsConverted(dreamId));
                model.addAttribute(COMMENTS_AMOUNT, this.commentService.getDreamCommentsConverted(dreamId).size());
                return SHOW_DREAM_TEMPLATE;
            }
        }
        return HOMEPAGE_REDIRECT;
    }

    @GetMapping ("update/{id}")
    public String editDream (Principal principal, Model model, @PathVariable long id) {
        if(dreamService.getDream(id).getAuthor().equals(userService.getUsernameByEmail(principal.getName()))) {
            model.addAttribute(DREAMLOG_FORM_HEADER_ID, "Please Edit the Dream");
            model.addAttribute(DREAMS_TEMPLATE_ID, this.dreamService.getOwnDreams(principal.getName()));
            model.addAttribute(DREAMS_TEMPLATE_NEW_DREAM, this.dreamService.getDream(id));

            return EDIT_DREAM_TEMPLATE;

        }

        return HOMEPAGE_REDIRECT;
    }

    @PostMapping ("update/{id}")
    public String saveDream (Principal principal, Model model,
                               @PathVariable long id,
                               @Valid @ModelAttribute (DREAMS_TEMPLATE_NEW_DREAM) DreamEntry newEntry,
                               BindingResult bindingResult) {

        if (!bindingResult.hasErrors ()) {
            DreamEntry current = this.dreamService.getDream (id);
            current.setTitle (newEntry.getTitle ());
            current.setType (newEntry.getType ());
            current.setDescription(newEntry.getDescription ());
            current.setVisibility(newEntry.getVisibility());
            this.dreamService.saveDream (current);
            return HOMEPAGE_REDIRECT;
        }
        else {
            model.addAttribute (DREAMLOG_FORM_HEADER_ID, "Please Fill in all Forms");
            model.addAttribute (DREAMS_TEMPLATE_ID, this.dreamService.getOwnDreams(principal.getName()));
            return EDIT_DREAM_TEMPLATE;
        }
    }

    @GetMapping("/public")
    public String displayPublicDreams (Principal principal, Model model, @ModelAttribute(DREAMS_TEMPLATE_NEW_DREAM) DreamEntry newEntry )
    {
        if (newEntry.getType() == null) {

            model.addAttribute(PUBLIC_DREAMS_TEMPLATE_ID, this.dreamService.getPublicDreams());
            model.addAttribute(USERNAME, userService.getUsernameByEmail(principal.getName()));
        }
        else{
            if (newEntry.getType().equals("")) {
                model.addAttribute(PUBLIC_DREAMS_TEMPLATE_ID, this.dreamService.getPublicDreams());
                model.addAttribute(USERNAME, userService.getUsernameByEmail(principal.getName()));
            }
            else{

                if(newEntry.getAuthor().equals("title"))
                {
                    model.addAttribute(PUBLIC_DREAMS_TEMPLATE_ID, this.dreamService.getPublicDreamsByTitle(newEntry.getType()));
                    model.addAttribute(USERNAME, userService.getUsernameByEmail(principal.getName()));
                }
                if(newEntry.getAuthor().equals("type"))
                {
                    model.addAttribute(PUBLIC_DREAMS_TEMPLATE_ID, this.dreamService.getPublicDreamsByType(newEntry.getType()));
                    model.addAttribute(USERNAME, userService.getUsernameByEmail(principal.getName()));

                }
                if(newEntry.getAuthor().equals("author"))
                {
                    model.addAttribute(PUBLIC_DREAMS_TEMPLATE_ID, this.dreamService.getPublicDreamsByAuthor(newEntry.getType()));
                    model.addAttribute(USERNAME, userService.getUsernameByEmail(principal.getName()));

                }
                            }
        }

        return SHOW_PUBLIC_DREAMS_TEMPLATE;
    }


}
