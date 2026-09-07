package ca.datamagic.radiotester.async;

import android.content.Context;

import ca.datamagic.radiotester.R;
import ca.datamagic.radiotester.dao.RadioStationDAO;
import ca.datamagic.radiotester.dto.RadioStationDTO;

public class CreateRadioStationAsync extends AsyncTaskBase<Void> {
    private RadioStationDAO dao = null;
    private RadioStationDTO dto = null;

    public CreateRadioStationAsync(Context context, RadioStationDTO dto) {
        this.dao = new RadioStationDAO(context, R.raw.appspot_com);
        this.dto = dto;
    }

    @Override
    protected AsyncTaskResult<Void> doInBackground() {
        try {
            this.dao.insert(this.dto);
            return new AsyncTaskResult<>();
        } catch (Throwable t) {
            return new AsyncTaskResult<>(t);
        }
    }
}
