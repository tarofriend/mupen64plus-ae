package paulscode.android.mupen64plusae;

import java.io.File;

import paulscode.android.mupen64plusae.util.Notifier;
import paulscode.android.mupen64plusae.util.Prompt;
import paulscode.android.mupen64plusae.util.Prompt.OnFileListener;
import paulscode.android.mupen64plusae.util.Prompt.OnTextListener;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;

public class GameMenuHandler
{
    private static final int NUM_SLOTS = 10;
    
    private final Activity mActivity;
    
    private MenuItem mSlotMenuItem;
    
    private int mSlot = 0;
    
    public GameMenuHandler( Activity activity )
    {
        mActivity = activity;
    }
    
    public void onCreateOptionsMenu( Menu menu )
    {
        // Inflate the in-game menu, record the 'Slot X' menu object for later
        mActivity.getMenuInflater().inflate( R.menu.game_activity, menu );
        mSlotMenuItem = menu.findItem( R.id.ingameSlot );
        setSlot( 0, false );
    }
    
    public void onOptionsItemSelected( MenuItem item )
    {
        switch( item.getItemId() )
        {
            case R.id.slot0:
                setSlot( 0, item );
                break;
            case R.id.slot1:
                setSlot( 1, item );
                break;
            case R.id.slot2:
                setSlot( 2, item );
                break;
            case R.id.slot3:
                setSlot( 3, item );
                break;
            case R.id.slot4:
                setSlot( 4, item );
                break;
            case R.id.slot5:
                setSlot( 5, item );
                break;
            case R.id.slot6:
                setSlot( 6, item );
                break;
            case R.id.slot7:
                setSlot( 7, item );
                break;
            case R.id.slot8:
                setSlot( 8, item );
                break;
            case R.id.slot9:
                setSlot( 9, item );
                break;
            case R.id.ingameQuicksave:
                saveSlot();
                break;
            case R.id.ingameQuickload:
                loadSlot();
                break;
            case R.id.ingameSave:
                saveStateFromPrompt();
                break;
            case R.id.ingameLoad:
                loadStateFromPrompt();
                break;
            case R.id.ingameReset:
                resetState();
                break;
            case R.id.ingameMenu:
                // Return to previous activity (MenuActivity)
                // It's easier just to finish so that everything will be reloaded next time
                mActivity.finish();
                break;
            default:
                break;
        }
    }
    
    private void setSlot( int value, MenuItem item )
    {
        setSlot( value );
        item.setChecked( true );
    }
    
    private void setSlot( int value )
    {
        setSlot( value, true );
    }
    
    private void setSlot( int value, boolean notify )
    {
        mSlot = value % NUM_SLOTS;
        NativeMethods.stateSetSlotEmulator( mSlot );
        if( notify )
            Notifier.showToast( mActivity, R.string.toast_savegameSlot, mSlot );
        if( mSlotMenuItem != null )
            mSlotMenuItem.setTitle( mActivity.getString( R.string.ingameSlot_title, mSlot ) );
    }
    
    private void saveSlot()
    {
        Notifier.showToast( mActivity, R.string.toast_savingSlot, mSlot );
        NativeMethods.stateSaveEmulator();
    }
    
    private void loadSlot()
    {
        Notifier.showToast( mActivity, R.string.toast_loadingSlot, mSlot );
        NativeMethods.stateLoadEmulator();
    }
    
    private void saveStateFromPrompt()
    {
        NativeMethods.pauseEmulator();
        CharSequence title = mActivity.getText( R.string.ingameSave_title );
        CharSequence hint = mActivity.getText( R.string.gameMenu_saveHint );
        Prompt.promptText( mActivity, title, null, hint, new OnTextListener()
        {
            @Override
            public void onText( CharSequence text, int which )
            {
                if( which == DialogInterface.BUTTON_POSITIVE )
                    saveState( text.toString() );
                NativeMethods.resumeEmulator();
            }
        } );
    }
    
    private void loadStateFromPrompt()
    {
        NativeMethods.pauseEmulator();
        CharSequence title = mActivity.getText( R.string.ingameLoad_title );
        File startPath = new File( Globals.userPrefs.gameSaveDir );
        Prompt.promptFile( mActivity, title, null, startPath, new OnFileListener()
        {
            @Override
            public void onFile( File file, int which )
            {
                if( which == DialogInterface.BUTTON_POSITIVE )
                    loadState( file );
                NativeMethods.resumeEmulator();
            }
        } );
    }
    
    private void saveState( final String filename )
    {
        final File file = new File( Globals.userPrefs.gameSaveDir + "/" + filename );
        if( file.exists() )
        {
            String title = mActivity.getString( R.string._confirmation );
            String message = mActivity.getString( R.string.gameMenu_confirmFile, filename );
            Prompt.promptConfirm( mActivity, title, message, new OnClickListener()
            {
                @Override
                public void onClick( DialogInterface dialog, int which )
                {
                    if( which == DialogInterface.BUTTON_POSITIVE )
                    {
                        Notifier.showToast( mActivity, R.string.toast_overwritingGame,
                                file.getName() );
                        NativeMethods.fileSaveEmulator( file.getAbsolutePath() );
                    }
                }
            } );
        }
        else
        {
            Notifier.showToast( mActivity, R.string.toast_savingGame, file.getName() );
            NativeMethods.fileSaveEmulator( file.getAbsolutePath() );
        }
    }
    
    private void loadState( File file )
    {
        Notifier.showToast( mActivity, R.string.toast_loadingGame, file.getName() );
        NativeMethods.fileLoadEmulator( file.getAbsolutePath() );
    }
    
    private void resetState()
    {
        NativeMethods.pauseEmulator();
        String title = mActivity.getString( R.string._confirmation );
        String message = mActivity.getString( R.string.gameMenu_confirmReset );
        Prompt.promptConfirm( mActivity, title, message, new OnClickListener()
        {
            @Override
            public void onClick( DialogInterface dialog, int which )
            {
                if( which == DialogInterface.BUTTON_POSITIVE )
                {
                    Notifier.showToast( mActivity, R.string.toast_resettingGame );
                    NativeMethods.resetEmulator();
                }
                NativeMethods.resumeEmulator();
            }
        } );
    }
}
