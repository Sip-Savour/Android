package com.sipandsavour.ui.auth;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.textfield.TextInputLayout;
import com.sipandsavour.R;
import com.sipandsavour.data.Repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 34)
public class LoginFragmentTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock AuthViewModel mockViewModel;
    @Mock NavController mockNavController;

    private MockedStatic<NavHostFragment> mockedNavHost;
    private MockedStatic<Repository> mockedRepository;

    // LiveData réels pour contrôler le mock
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // On configure le mock pour qu'il renvoie nos LiveData de test
        when(mockViewModel.getIsLoading()).thenReturn(isLoading);
        when(mockViewModel.getErrorMessage()).thenReturn(errorMessage);
        when(mockViewModel.getLoginSuccess()).thenReturn(loginSuccess);

        // Mock de la navigation
        mockedNavHost = mockStatic(NavHostFragment.class);
        mockedNavHost.when(() -> NavHostFragment.findNavController(any(Fragment.class)))
                .thenReturn(mockNavController);

        // Mock du Repository (pour éviter les crashs du ViewModel réel s'il y en a)
        mockedRepository = mockStatic(Repository.class);
    }

    @After
    public void tearDown() {
        if (mockedNavHost != null) mockedNavHost.close();
        if (mockedRepository != null) mockedRepository.close();
    }

    @Test
    public void loginSuccess_navigatesToHome() {
        // 1. Lancer le scenario
        FragmentScenario<LoginFragment> scenario = FragmentScenario.launchInContainer(
                LoginFragment.class, null, R.style.Theme_SipSavour, (FragmentFactory) null);

        scenario.onFragment(fragment -> {
            // --- LE HACK CRUCIAL ---
            // On récupère le ViewModel de l'activité réelle du fragment
            // et on vérifie qu'on utilise bien notre mock ou on déclenche l'action sur le VM de l'activité
            AuthViewModel activityViewModel = new ViewModelProvider(fragment.requireActivity()).get(AuthViewModel.class);

            // On déclenche le succès sur le LiveData de l'instance RÉELLE utilisée par le fragment
            // (Si ton loginSuccess était static, ça marcherait direct, sinon on utilise l'instance de l'activité)
            activityViewModel.getLoginSuccess().observeForever(s -> {}); // Force l'attachement

            // On simule le succès
            // Note : Si tu as bien enlevé le 'static' dans AuthViewModel, c'est cette instance qui compte
            if (activityViewModel.getLoginSuccess() instanceof MutableLiveData) {
                ((MutableLiveData<Boolean>) activityViewModel.getLoginSuccess()).setValue(true);
            }
        });

        // 2. Vérification
        verify(mockNavController).navigate(R.id.action_login_to_home);
    }

    @Test
    public void clickLogin_withEmptyEmail_showsError() {
        FragmentScenario<LoginFragment> scenario = FragmentScenario.launchInContainer(
                LoginFragment.class, null, R.style.Theme_SipSavour, (FragmentFactory) null);

        scenario.onFragment(fragment -> {
            // Action : Clic sans remplir
            fragment.getView().findViewById(R.id.btnLogin).performClick();

            // Vérification
            TextInputLayout tilEmail = fragment.getView().findViewById(R.id.tilEmail);
            assertEquals(fragment.getString(R.string.validation_email_required), tilEmail.getError());
        });
    }
}