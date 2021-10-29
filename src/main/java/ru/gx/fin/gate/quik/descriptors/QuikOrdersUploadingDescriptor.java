package ru.gx.fin.gate.quik.descriptors;

import ru.gx.fin.gate.quik.model.internal.QuikDeal;
import ru.gx.fin.gate.quik.model.internal.QuikDealsPackage;
import ru.gx.fin.gate.quik.model.internal.QuikOrder;
import ru.gx.fin.gate.quik.model.internal.QuikOrdersPackage;
import ru.gx.kafka.upload.OutcomeTopicUploadingDescriptor;
import ru.gx.kafka.upload.OutcomeTopicUploadingDescriptorsDefaults;

public class QuikOrdersUploadingDescriptor extends OutcomeTopicUploadingDescriptor<QuikOrder, QuikOrdersPackage> {
    public QuikOrdersUploadingDescriptor(String topic, OutcomeTopicUploadingDescriptorsDefaults defaults) {
        super(topic, defaults);
    }
}
