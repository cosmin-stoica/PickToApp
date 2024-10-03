package com.example.picktolightapp.Live;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.picktolightapp.Connectivity.DispositiviHandler;
import com.example.picktolightapp.DialogsHandler;
import com.example.picktolightapp.GlobalVariables;
import com.example.picktolightapp.MainActivity;
import com.example.picktolightapp.Model_DB.Dispositivo.Dispositivo;
import com.example.picktolightapp.Model_DB.Dispositivo.DispositivoTable;
import com.example.picktolightapp.Model_DB.Operation.Operation;
import com.example.picktolightapp.Model_DB.PermissionOperations.PermissionOperationsTable;
import com.example.picktolightapp.Model_DB.User.CurrentUser;
import com.example.picktolightapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class LiveFragment extends Fragment {

    GridLayout gridLayout;

    boolean isDev = true;
    List<CardView> cardViews = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.live_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //List<Dispositivo> currentDispositivi = DispositivoTable.getAllDispositivi(requireContext());
        List<Dispositivo> currentDispositivi =   GlobalVariables.getInstance().getCurrentDispositivi();

        GlobalVariables.getInstance().setCurrentDispositivi(currentDispositivi);
        gridLayout = view.findViewById(R.id.gridLayout);
        Button btnRilevaDispositivi = view.findViewById(R.id.btnRilevaDispositivi);

        btnRilevaDispositivi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PermissionOperationsTable.userHasPermission(requireContext(), CurrentUser.getInstance(), Operation.DETECT_DISP, true)) {
                    return;
                }
                handleRilevaDispositivi();
            }
        });

        createCards(currentDispositivi);

    }

    private void createNewCard(Dispositivo dispositivo) {
        CardView cardView = createCardView();
        cardViews.add(cardView);
        FrameLayout frameLayout = createFrameLayout();


        //StateListAnimator stateListAnimator = AnimatorInflater.loadStateListAnimator(requireContext(), R.animator.home_button_press_animation);
        //cardView.setStateListAnimator(stateListAnimator);

        ImageView imageView = createImageView();
        showDispositivoImage(dispositivo, imageView);
        frameLayout.addView(imageView);

        TextView textView = createTextView(dispositivo);
        frameLayout.addView(textView);

        addTopRightIconIfNecessary(dispositivo, frameLayout, textView);

        cardView.addView(frameLayout);
        gridLayout.addView(cardView);
    }

    private CardView createCardView() {
        CardView cardView = new CardView(requireContext());
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int numColumns = 4;
        int totalMargin = 64 * (numColumns + 1);
        int cardWidth = (screenWidth - totalMargin) / numColumns;

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = cardWidth;
        params.height = cardWidth;
        params.setMargins(32, 32, 32, 32);
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED);
        cardView.setLayoutParams(params);
        cardView.setCardElevation(0);
        cardView.setRadius(40);
        cardView.setCardBackgroundColor(0x77FFFFFF);


        return cardView;
    }

    private FrameLayout createFrameLayout() {
        return new FrameLayout(requireContext()) {
            @Override
            public boolean performClick() {
                super.performClick();
                return true;
            }
        };
    }

    private ImageView createImageView() {
        ImageView imageView = new ImageView(requireContext());
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        imageParams.gravity = Gravity.CENTER;
        imageView.setLayoutParams(imageParams);
        return imageView;
    }

    private TextView createTextView(Dispositivo dispositivo) {
        TextView textView = new TextView(requireContext());
        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        textParams.gravity = Gravity.CENTER;
        textView.setLayoutParams(textParams);
        textView.setText(String.valueOf(dispositivo.getId()));
        textView.setTextColor(0x77FFFFFF);
        textView.setTextSize(130);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    private void addTopRightIconIfNecessary(Dispositivo dispositivo, FrameLayout frameLayout, TextView textView) {
        ImageView topRightImageView = new ImageView(requireContext());
        if (dispositivo.getQty() <= dispositivo.getQty_min()) {
            FrameLayout.LayoutParams topRightParams = new FrameLayout.LayoutParams(60, 60);
            topRightParams.gravity = Gravity.TOP | Gravity.END;
            topRightParams.setMargins(16, 16, 16, 16);
            topRightImageView.setLayoutParams(topRightParams);
            topRightImageView.setImageResource(R.drawable.warning_icon);
            frameLayout.addView(topRightImageView);
        }
        setupGestureDetection(frameLayout, textView, topRightImageView, dispositivo);
    }


    private void setupGestureDetection(FrameLayout frameLayout, TextView textView, ImageView topRightImageView, Dispositivo dispositivo) {
        GestureDetector gestureDetector = new GestureDetector(requireContext(), new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 50;
            private static final int SWIPE_VELOCITY_THRESHOLD = 50;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX < 0) {
                            toggleCardDarken(frameLayout, textView, topRightImageView, dispositivo);
                            return true; // Gesto di swipe gestito, non eseguire il click
                        }
                    }
                }
                return false; // Il gesto non è stato uno swipe
            }
        });

        NavController navController = NavHostFragment.findNavController(this);

        frameLayout.setOnTouchListener((v, event) -> {
            boolean gestureHandled = gestureDetector.onTouchEvent(event);

            // Se il gesto non è stato gestito da gestureDetector (quindi non è uno swipe)
            if (event.getAction() == MotionEvent.ACTION_UP && !gestureHandled) {
                v.performClick();
                navController.navigate(R.id.action_to_LiveVisualizeFragment);
                MainActivity.setLogoInvisible();
                GlobalVariables.getInstance().setLastDestinationId(R.id.LiveFragment);
                GlobalVariables.getInstance().setDispositivoToSee(dispositivo);
            }

            return gestureHandled;
        });

        frameLayout.setOnClickListener(v -> {
            // Gestisci ulteriori azioni di click qui se necessario
        });
    }


    private void toggleCardDarken(FrameLayout frameLayout, TextView textView, ImageView topRightImageView, Dispositivo dispositivo) {
        if (frameLayout.getAlpha() == 1f) {
            animateCardFlipAndDarken(frameLayout);
            hideTextAndIcon(textView, topRightImageView, frameLayout, dispositivo);
        } else {
            animateCardFlipAndReset(frameLayout);
            showTextAndIcon(textView, topRightImageView, frameLayout);
        }
    }

    private void hideTextAndIcon(TextView textView, ImageView topRightImageView, FrameLayout frameLayout, Dispositivo dispositivo) {
        textView.setVisibility(View.GONE); // Nascondi il testo
        topRightImageView.setVisibility(View.GONE); // Nascondi l'immagine in alto a destra

        if (frameLayout.getParent() instanceof CardView) {
            CardView cardView = (CardView) frameLayout.getParent();
            cardView.setCardBackgroundColor(0x44FFFFFF);
        }

        // Creare un contenitore per i nuovi TextView, indipendente dall'animazione
        FrameLayout nonRotatingContainer = new FrameLayout(requireContext());
        FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        nonRotatingContainer.setLayoutParams(containerParams);

        // Crea due nuove TextView e aggiungile al contenitore separato
        TextView newTextView1 = new TextView(requireContext());
        TextView newTextView2 = new TextView(requireContext());

        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params1.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params1.topMargin = 80;

        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params2.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params2.topMargin = 120;

        if (dispositivo.getComponente() != null)
            newTextView1.setText(dispositivo.getComponente().getName());
        newTextView2.setText(String.valueOf(dispositivo.getQty()) + " Quantità");

        newTextView1.setTextColor(Color.WHITE);
        newTextView1.setTextSize(30);
        newTextView1.setTypeface(null, Typeface.BOLD);

        newTextView2.setTextColor(Color.parseColor("#FFB800"));
        newTextView2.setTextSize(33);
        newTextView2.setTypeface(null, Typeface.BOLD);

        // Aggiungi le TextView al contenitore separato
        nonRotatingContainer.addView(newTextView1, params1);
        nonRotatingContainer.addView(newTextView2, params2);

        // Aggiungi il contenitore separato alla CardView o al FrameLayout
        ((ViewGroup) frameLayout.getParent()).addView(nonRotatingContainer);
    }


    private void showTextAndIcon(TextView textView, ImageView topRightImageView, FrameLayout frameLayout) {
        textView.setVisibility(View.VISIBLE);
        topRightImageView.setVisibility(View.VISIBLE);

        if (frameLayout.getParent() instanceof CardView) {
            CardView cardView = (CardView) frameLayout.getParent();
            cardView.setCardBackgroundColor(0x77FFFFFF);
        }

        // Rimuovi il contenitore che contiene i due nuovi TextView
        ViewGroup parent = (ViewGroup) frameLayout.getParent();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof FrameLayout && child != frameLayout) {
                parent.removeView(child);
                break; // Trova e rimuovi solo una volta
            }
        }
    }

    private void animateCardFlipAndDarken(FrameLayout frameLayout) {
        ObjectAnimator flipAnimator = ObjectAnimator.ofFloat(frameLayout, "rotationY", 0f, 180f);
        flipAnimator.setDuration(500);
        flipAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                frameLayout.setAlpha(0.3f);
                applyBlurEffect(frameLayout, 25);
            }
        });
        flipAnimator.start();
    }

    private void animateCardFlipAndReset(FrameLayout frameLayout) {
        ObjectAnimator flipBackAnimator = ObjectAnimator.ofFloat(frameLayout, "rotationY", 180f, 0f);
        flipBackAnimator.setDuration(500);
        flipBackAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                frameLayout.setAlpha(1f); // Ripristina la trasparenza della card

                // Rimuovere l'effetto di sfocatura
                frameLayout.setBackground(null); // Rimuovi lo sfondo sfocato
            }
        });
        flipBackAnimator.start();
    }

    private void applyBlurEffect(FrameLayout frameLayout, float blurRadius) {
        // Creare una bitmap dalla View
        frameLayout.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(frameLayout.getDrawingCache());
        frameLayout.setDrawingCacheEnabled(false);

        // Applicare la sfocatura alla bitmap
        Bitmap blurredBitmap = blurBitmap(bitmap, blurRadius);

        // Impostare la bitmap sfocata come sfondo della FrameLayout
        frameLayout.setBackground(new BitmapDrawable(getResources(), blurredBitmap));
    }

    private Bitmap blurBitmap(Bitmap bitmap, float radius) {
        // Limita il raggio della sfocatura a 25
        radius = Math.min(radius, 25);

        // Riduci temporaneamente la dimensione della bitmap per aumentare l'effetto di sfocatura
        int width = Math.round(bitmap.getWidth() * 0.4f); // Riduci del 40%
        int height = Math.round(bitmap.getHeight() * 0.4f);
        Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);

        // Crea la bitmap di output ridimensionata
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        // Crea RenderScript
        final RenderScript renderScript = RenderScript.create(requireContext());
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);

        // Applica la sfocatura
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(radius); // Imposta un raggio massimo di 25
        theIntrinsic.setInput(tmpIn);

        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        // Ridimensiona la bitmap sfocata alla dimensione originale
        outputBitmap = Bitmap.createScaledBitmap(outputBitmap, bitmap.getWidth(), bitmap.getHeight(), false);

        return outputBitmap;
    }


    private void showDispositivoImage(Dispositivo dispositivo, ImageView imageView) {
        if (dispositivo.getComponente() != null) {
            String imageUri = dispositivo.getComponente().getImg();
            if (imageUri != null && !imageUri.isEmpty()) {
                Picasso.get().load(imageUri).into(imageView);
            }
        }
    }


    public interface OnDispositiviRilevatiListener {
        void onDispositiviRilevati(); // Metodo senza argomenti e senza ritorno
    }

    private void handleRilevaDispositivi() {
        DispositiviHandler.getDispositiviRilevato(requireContext(), getLayoutInflater(), () -> {
            removeAllCardViews();
            createCards(GlobalVariables.getInstance().getCurrentDispositivi());
        });
    }

    private void createCards(List<Dispositivo> currentDispositivi){
        if(isDev) {
            for (Dispositivo dispositivo : currentDispositivi) {
                createNewCard(dispositivo);
            }
        }
        else{
            for (Dispositivo dispositivo : currentDispositivi) {
                if(dispositivo.isRilevato())
                    createNewCard(dispositivo);
            }
        }
    }

    private void removeAllCardViews(){
        for(CardView cardView : cardViews){
            gridLayout.removeView(cardView);
        }
        cardViews.clear();
    }
}
