package org.geogebra.web.html5.multiuser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawLocus;
import org.geogebra.common.euclidian.draw.DrawSegment;
import org.geogebra.common.euclidian.draw.HasTransformation;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.RectangleTransformable;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.timer.client.Timer;

class User {

	private final TooltipChip tooltip;
	// we are storing the labels since the geo might be recreated
	private final Map<String, Timer> interactions;
	private final GColor color;

	User(String user, GColor color) {
		this.tooltip = new TooltipChip(user, color);
		this.interactions = new HashMap<>();
		this.color = color;
	}

	public void addInteraction(EuclidianView view, String label) {
		interactions.compute(label, (k, v) -> {
			if (v == null) {
				v = new Timer() {
					@Override
					public void run() {
						interactions.remove(label);
						view.repaintView();
					}
				};
			}

			v.schedule(4000);
			return v;
		});

		view.repaintView();
	}

	public void removeInteraction(String label) {
		interactions.remove(label);
	}

	public void paintInteractionBoxes(EuclidianView view, GGraphics2D graphics) {
		SelectionManager selection = view.getApplication().getSelectionManager();
		List<GeoElement> geos = interactions.keySet().stream()
				.map((label) -> view.getApplication().getKernel().lookupLabel(label))
				.filter((geo) -> !selection.containsSelectedGeo(geo))
				.collect(Collectors.toList());

		graphics.setColor(color);
		if (geos.size() == 0) {
			tooltip.hide();
		} else {
			showTooltipBy((AppW) view.getApplication(), geos.get(0));
		}

		if (geos.size() == 1) {
			GeoElement geo = geos.get(0);
			Drawable d = (Drawable) view.getDrawableFor(geo);
			if (d instanceof HasTransformation) {
				RectangleTransformable transformableGeo = (RectangleTransformable) geo;
				graphics.saveTransform();
				graphics.transform(((HasTransformation) d).getTransform());
				graphics.draw(AwtFactory.getPrototype().newRectangle(
						(int) transformableGeo.getWidth(),
						(int) transformableGeo.getHeight()
				));
				graphics.restoreTransform();
			} else if (d instanceof DrawLocus || d instanceof DrawSegment) {
				GBasicStroke current = graphics.getStroke();
				graphics.setStroke(AwtFactory.getPrototype()
						.newBasicStroke(geo.getLineThickness() + 2, GBasicStroke.CAP_ROUND,
								GBasicStroke.JOIN_ROUND));
				GShape gp = d instanceof DrawLocus
						? ((DrawLocus) d).getPath()
						: ((DrawSegment) d).getLine();
				graphics.draw(gp);
				graphics.setStroke(current);
			} else if (d != null) {
				graphics.draw(d.getBoundsForStylebarPosition());
			}
		} else if (geos.size() > 1) {
			graphics.draw(view.getEuclidianController().calculateBounds(geos));
		}
	}

	private void showTooltipBy(AppW app, GeoElement geo) {
		DrawableND drawable = app.getActiveEuclidianView().getDrawableFor(geo);
		if (drawable != null) {
			GRectangle2D bounds = drawable.getBoundsForStylebarPosition();
			double x = bounds.getMaxX() + getOffsetX(app);
			double y = bounds.getMinY() + getOffsetY(app);
			app.getAppletFrame().add(tooltip);
			tooltip.show(x, y);
		}
	}

	private double getOffsetY(AppW app) {
		return app.getActiveEuclidianView().getAbsoluteTop() - app.getAbsTop();
	}

	private double getOffsetX(AppW app) {
		return app.getActiveEuclidianView().getAbsoluteLeft() - app.getAbsLeft();
	}
}
