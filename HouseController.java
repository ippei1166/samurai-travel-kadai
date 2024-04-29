package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entitiy.House;
import com.example.samuraitravel.form.ReservationInputForm;
import com.example.samuraitravel.form.ReviewForm;
import com.example.samuraitravel.repository.HouseRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/houses")
public class HouseController {
    private final HouseRepository houseRepository;        
    
    public HouseController(HouseRepository houseRepository) {
        this.houseRepository = houseRepository;            
    }     
  
    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
                        @RequestParam(name = "area", required = false) String area,
                        @RequestParam(name = "price", required = false) Integer price,  
                        @RequestParam(name = "order", required = false) String order,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                        Model model) 
    {
        Page<House> housePage;
                
        if (keyword != null && !keyword.isEmpty()) {            
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByNameLikeOrAddressLikeOrderByPriceAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
            } else {
                housePage = houseRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
            }            
        } else if (area != null && !area.isEmpty()) {            
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", pageable);
            } else {
                housePage = houseRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
            }            
        } else if (price != null) {            
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
            } else {
                housePage = houseRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
            }            
        } else {            
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findAllByOrderByPriceAsc(pageable);
            } else {
                housePage = houseRepository.findAllByOrderByCreatedAtDesc(pageable);   
            }            
        }                
        
        model.addAttribute("housePage", housePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("area", area);
        model.addAttribute("price", price); 
        model.addAttribute("order", order);
        
        return "houses/index";
    }
    
    @GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id, Model model) {
        House house = houseRepository.getReferenceById(id);
        
        model.addAttribute("house", house);  
        model.addAttribute("reservationInputForm", new ReservationInputForm());
        
        return "houses/show";
    }  

    @GetMapping("/review")
    public String review(Model model) {
        model.addAttribute("reviewForm", new ReviewForm()); // 新しいレビューフォームを作成して画面に渡す
        return "houses/review"; // "review"という名前のHTMLページを表示する
    }
    // フォームから送信されたデータを受け取る
    @PostMapping("/submitReview")
    public String submitReview(@Valid ReviewForm reviewForm, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            // フォームのデータに問題があれば、再度フォームを表示
            return "houses/review";
        }
        // フォームのデータが正しければ、処理を続ける
        attributes.addFlashAttribute("message", "レビューが送信されました！");
        return "redirect:/showReviews";
    }
    @SessionAttributes("reviewForm") 
    public class ReviewController {

        @GetMapping("/review")
        public String review(Model model) {
            if (!model.containsAttribute("reviewForm")) {
                model.addAttribute("reviewForm", new ReviewForm());
            }
            return "houses/review";
        }
        @GetMapping("/showReviews")
        public String showReviews(Model model) {
            
            return "houses/showReviews"; // レビュー一覧ページを表示
        }
       
    }
  
}



