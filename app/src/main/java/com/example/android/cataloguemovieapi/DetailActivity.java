package com.example.android.cataloguemovieapi;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.cataloguemovieapi.factory.DetailedMovieViewModelFactory;
import com.example.android.cataloguemovieapi.fragment.NowPlayingMovieFragment;
import com.example.android.cataloguemovieapi.item.MovieItems;
import com.example.android.cataloguemovieapi.model.DetailedMovieViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    // Setup views bedasarkan id yang ada di layout xml
    @BindView(R.id.detailed_poster_image) ImageView imageViewDetailedPosterImage;
    @BindView(R.id.detailed_movie_title_text) TextView textViewDetailedMovieTitle;
    @BindView(R.id.detailed_movie_tagline_text) TextView textViewDetailedMovieTagline;
    @BindView(R.id.detailed_movie_status_text) TextView textViewDetailedMovieStatus;
    @BindView(R.id.detailed_movie_rating_text) TextView textViewDetailedMovieRating;
    @BindView(R.id.detailed_movie_languages_text) TextView textViewDetailedMovieLanguage;
    @BindView(R.id.detailed_movie_genres_text) TextView textViewDetailedMovieGenres;
    @BindView(R.id.detailed_movie_release_date_text) TextView textViewDetailedMovieReleaseDate;
    @BindView(R.id.detailed_movie_overview_text) TextView textViewDetailedMovieOverview;
    @BindView(R.id.languages_spoken) TextView languageSpoken;
    @BindView(R.id.genres) TextView genres;
    @BindView(R.id.release_date) TextView releaseDate;
    @BindView(R.id.overview) TextView overview;
    private int detailedMovieId;
    private String detailedMovieTitle;
    // Set layout value untuk dapat menjalankan process loading data
    @BindView(R.id.detailed_progress_bar) ProgressBar detailedProgressBar;

    @BindView(R.id.detailed_app_bar) AppBarLayout detailedAppBarLayout;
    @BindView(R.id.detailed_toolbar) Toolbar detailedToolbar;

    // Gunakan BuildConfig untuk menjaga credential
    private String baseImageUrl = BuildConfig.IMAGE_MOVIE_URL;

    private DetailedMovieViewModel detailedMovieViewModel;

    private Observer<ArrayList<MovieItems>> detailedMovieObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        setSupportActionBar(detailedToolbar);

        // Get intent untuk mendapatkan id dan title dari {@link MainActivity}
        detailedMovieId = getIntent().getIntExtra(NowPlayingMovieFragment.MOVIE_ID_DATA, 0);
        detailedMovieTitle = getIntent().getStringExtra(NowPlayingMovieFragment.MOVIE_TITLE_DATA);

        // Cek kalo ada action bar
        if(getSupportActionBar() != null){
            // Set action bar title untuk DetailActivity
            getSupportActionBar().setTitle(detailedMovieTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Set visiblity of views ketika sedang dalam meretrieve data
        imageViewDetailedPosterImage.setVisibility(View.INVISIBLE);
        textViewDetailedMovieTitle.setVisibility(View.INVISIBLE);
        textViewDetailedMovieGenres.setVisibility(View.INVISIBLE);
        textViewDetailedMovieLanguage.setVisibility(View.INVISIBLE);
        textViewDetailedMovieOverview.setVisibility(View.INVISIBLE);
        textViewDetailedMovieRating.setVisibility(View.INVISIBLE);
        textViewDetailedMovieReleaseDate.setVisibility(View.INVISIBLE);
        textViewDetailedMovieStatus.setVisibility(View.INVISIBLE);
        textViewDetailedMovieTagline.setVisibility(View.INVISIBLE);
        languageSpoken.setVisibility(View.INVISIBLE);
        genres.setVisibility(View.INVISIBLE);
        releaseDate.setVisibility(View.INVISIBLE);
        overview.setVisibility(View.INVISIBLE);
        detailedProgressBar.setVisibility(View.VISIBLE);

        // Panggil MovieViewModel dengan menggunakan ViewModelFactory sebagai parameter tambahan (dan satu-satunya pilihan) selain activity
        detailedMovieViewModel = ViewModelProviders.of(this, new DetailedMovieViewModelFactory(this.getApplication(), detailedMovieId)).get(DetailedMovieViewModel.class);

        // Buat observer object untuk mendisplay data ke UI
        detailedMovieObserver = createObserver();

        // Tempelkan Observer ke LiveData object
        detailedMovieViewModel.getDetailedMovie().observe(this, detailedMovieObserver);

        detailedAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isAppBarLayoutShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                if(scrollRange + verticalOffset == 0){
                    isAppBarLayoutShow = true;
                } else if(isAppBarLayoutShow){
                    isAppBarLayoutShow = false;
                }

            }
        });
    }

    private Observer<ArrayList<MovieItems>> createObserver(){
        Observer<ArrayList<MovieItems>> observer = new Observer<ArrayList<MovieItems>>() {
            @Override
            public void onChanged(@Nullable ArrayList<MovieItems> detailedMovieItems) {
                // Ketika data selesai di load, maka kita akan mendapatkan data dan menghilangkan progress bar
                // yang menandakan bahwa loadingnya sudah selesai
                imageViewDetailedPosterImage.setVisibility(View.VISIBLE);
                textViewDetailedMovieTitle.setVisibility(View.VISIBLE);
                textViewDetailedMovieGenres.setVisibility(View.VISIBLE);
                textViewDetailedMovieLanguage.setVisibility(View.VISIBLE);
                textViewDetailedMovieOverview.setVisibility(View.VISIBLE);
                textViewDetailedMovieRating.setVisibility(View.VISIBLE);
                textViewDetailedMovieReleaseDate.setVisibility(View.VISIBLE);
                textViewDetailedMovieStatus.setVisibility(View.VISIBLE);
                textViewDetailedMovieTagline.setVisibility(View.VISIBLE);
                languageSpoken.setVisibility(View.VISIBLE);
                genres.setVisibility(View.VISIBLE);
                releaseDate.setVisibility(View.VISIBLE);
                overview.setVisibility(View.VISIBLE);
                detailedProgressBar.setVisibility(View.GONE);

                // Set semua data ke dalam detail activity
                // Load image jika ada poster path
                Picasso.get().load(baseImageUrl + detailedMovieItems.get(0).getMoviePosterPath()).into(imageViewDetailedPosterImage);

                textViewDetailedMovieTitle.setText(detailedMovieItems.get(0).getMovieTitle());

                textViewDetailedMovieTagline.setText("\"" + detailedMovieItems.get(0).getMovieTagline() + "\"");

                // Set textview content in detailed movie runtime to contain a variety of different colors
                Spannable statusWord = new SpannableString(getString(R.string.span_movie_detail_status) + " ");
                statusWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, statusWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textViewDetailedMovieStatus.setText(statusWord);
                Spannable statusDetailedMovie = new SpannableString(detailedMovieItems.get(0).getMovieStatus());
                statusDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, statusDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textViewDetailedMovieStatus.append(statusDetailedMovie);

                // Set textview content in detailed movie rating to contain a variety of different colors
                Spannable ratingWord = new SpannableString(getString(R.string.span_movie_detail_rating) + " ");
                ratingWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textViewDetailedMovieRating.setText(ratingWord);
                Spannable ratingDetailedMovie = new SpannableString(detailedMovieItems.get(0).getMovieRatings());
                ratingDetailedMovie.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, ratingDetailedMovie.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textViewDetailedMovieRating.append(ratingDetailedMovie);

                Spannable ratingFromWord = new SpannableString(" " + getString(R.string.span_movie_detail_from) + " ");
                ratingFromWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingFromWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textViewDetailedMovieRating.append(ratingFromWord);

                Spannable ratingDetailedMovieVotes = new SpannableString(detailedMovieItems.get(0).getMovieRatingsVote());
                ratingDetailedMovieVotes.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, ratingDetailedMovieVotes.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textViewDetailedMovieRating.append(ratingDetailedMovieVotes);

                Spannable ratingVotesWord = new SpannableString(" " + getString(R.string.span_movie_detail_votes));
                ratingVotesWord.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ratingVotesWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textViewDetailedMovieRating.append(ratingVotesWord);

                textViewDetailedMovieLanguage.setText(detailedMovieItems.get(0).getMovieLanguages());

                textViewDetailedMovieGenres.setText(detailedMovieItems.get(0).getMovieGenres());

                textViewDetailedMovieReleaseDate.setText(detailedMovieItems.get(0).getMovieReleaseDate());

                textViewDetailedMovieOverview.setText(detailedMovieItems.get(0).getMovieOverview());
            }
        };
        return observer;
    }
}

