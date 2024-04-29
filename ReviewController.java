package com.example.samuraitravel.controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entitiy.Review;
import com.example.samuraitravel.form.ReviewForm;
import com.example.samuraitravel.service.ReviewService;

import jakarta.validation.Valid;


@SessionAttributes("reviewForm")
public class ReviewController {

    private final ReviewService reviewService;

   
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/new")
    public String showReviewForm(Model model) {
        // 新しいレビューフォームを初期化してモデルに追加
        if (!model.containsAttribute("reviewForm")) {
            model.addAttribute("reviewForm", new ReviewForm());
        }
        return "houses/review";
    }

    @PostMapping("/submit")
    public String submitReview(@Valid ReviewForm reviewForm, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // フォームのバリデーションエラーがある場合は、フォームに戻る
            return "houses/review";
        }
        // ReviewForm から Review エンティティに変換
        Review newReview = convertToEntity(reviewForm);
        reviewService.saveReview(newReview); // レビューを保存
        redirectAttributes.addFlashAttribute("message", "レビューが正常に送信されました");
        return "redirect:/reviews/show"; // レビュー一覧ページへリダイレクト
    }

    private Review convertToEntity(ReviewForm reviewForm) {
        // ReviewForm のデータを Review エンティティに変換するロジック
        return new Review();
    }

    @GetMapping("/show")
    public String showReviews(Model model) {
        // データベースから全レビューを取得してモデルに追加
        model.addAttribute("reviews", reviewService.findAllReviews());
        return "houses/showReviews"; // レビュー一覧表示ページ
    }
}


